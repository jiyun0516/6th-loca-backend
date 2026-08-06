package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.review.ReviewCreateRequestDto;
import gdg.hongik.loca.dto.review.ReviewResponseDto;
import gdg.hongik.loca.entity.Tag;
import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.entity.VisitTag;
import gdg.hongik.loca.exception.TagNotFoundException;
import gdg.hongik.loca.exception.VisitRecordNotFoundException;
import gdg.hongik.loca.repository.TagRepository;
import gdg.hongik.loca.repository.VisitRecordRepository;
import gdg.hongik.loca.repository.VisitTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// 방문 후기(리뷰) 도메인 서비스 계층
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final VisitRecordRepository visitRecordRepository;
    private final VisitTagRepository visitTagRepository;
    private final TagRepository tagRepository;
    private final UserPreferenceUpdater userPreferenceUpdater;
    private final PlacePreferenceUpdater placePreferenceUpdater;

    // 임시 userId, JWT 도입 시 토큰에서 추출로 교체
    private static final Integer TEMP_USER_ID = 1;

    // 방문 후기 생성
    // - VisitRecord 저장 -> visit_tags 저장 -> 선호도 갱신 순서
    // - visitedAt 미수신 시 기록 시각으로 대체
    @Transactional
    public ReviewResponseDto create(ReviewCreateRequestDto request) {
        VisitRecord record = VisitRecord.builder()
                .userId(TEMP_USER_ID)
                .placeId(request.getPlaceId())
                .title(request.getTitle())
                .content(request.getContent())
                .companion(request.getCompanion())
                .keywords(toSet(request.getKeywords()))
                .imageUrls(toList(request.getImageUrls()))
                .visitedAt(request.getVisitedAt() == null ? OffsetDateTime.now() : request.getVisitedAt())
                .build();
        VisitRecord saved = visitRecordRepository.save(record);

        List<Integer> tagIds = saveTags(saved.getVisitId(), request.getTagIds());
        refreshPreferences(saved.getPlaceId());

        return ReviewResponseDto.from(saved, tagIds);
    }

    // 내 방문 후기 목록
    // - visitedAt 내림차순
    public List<ReviewResponseDto> list() {
        return visitRecordRepository.findByUserIdOrderByVisitedAtDesc(TEMP_USER_ID).stream()
                .map(r -> ReviewResponseDto.from(r, findTagIds(r.getVisitId())))
                .toList();
    }

    // 내 방문 후기 상세
    // - 키워드/태그/이미지 포함
    public ReviewResponseDto detail(Long visitId) {
        VisitRecord record = findOwned(visitId);
        return ReviewResponseDto.from(record, findTagIds(visitId));
    }

    // 내 방문 후기 수정
    // - dirty checking
    // - 키워드/이미지 컬렉션과 태그는 전체 교체
    @Transactional
    public ReviewResponseDto update(Long visitId, ReviewCreateRequestDto request) {
        VisitRecord record = findOwned(visitId);
        record.setTitle(request.getTitle());
        record.setContent(request.getContent());
        record.setCompanion(request.getCompanion());
        record.setKeywords(toSet(request.getKeywords()));
        record.setImageUrls(toList(request.getImageUrls()));
        if (request.getVisitedAt() != null) {
            record.setVisitedAt(request.getVisitedAt());
        }

        visitTagRepository.deleteByVisitId(visitId);
        List<Integer> tagIds = saveTags(visitId, request.getTagIds());
        refreshPreferences(record.getPlaceId());

        return ReviewResponseDto.from(record, tagIds);
    }

    // 내 방문 후기 삭제(하드 삭제)
    @Transactional
    public void delete(Long visitId) {
        VisitRecord record = findOwned(visitId);
        Integer placeId = record.getPlaceId();

        visitTagRepository.deleteByVisitId(visitId);
        visitRecordRepository.delete(record);
        refreshPreferences(placeId);
    }

    // 소유 단건 조회 헬퍼
    // - 미존재/소유자 불일치 -> VisitRecordNotFoundException
    private VisitRecord findOwned(Long visitId) {
        return visitRecordRepository.findByVisitIdAndUserId(visitId, TEMP_USER_ID)
                .orElseThrow(() -> new VisitRecordNotFoundException(visitId));
    }

    // 선택 태그 저장
    // - 같은 리뷰 안의 중복 tagId는 하나로 합침
    // - 미존재 tagId -> TagNotFoundException
    private List<Integer> saveTags(Long visitId, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }

        List<Integer> distinctIds = tagIds.stream().distinct().toList();

        // tagId 검증
        Set<Integer> found = tagRepository.findAllById(distinctIds).stream()
                .map(Tag::getTagId)
                .collect(Collectors.toSet());
        List<Integer> missing = distinctIds.stream()
                .filter(id -> !found.contains(id))
                .toList();
        if (!missing.isEmpty()) {
            throw new TagNotFoundException(missing);
        }

        List<VisitTag> rows = distinctIds.stream()
                .map(tagId -> VisitTag.builder()
                        .visitId(visitId)
                        .tagId(tagId)
                        .build())
                .toList();
        visitTagRepository.saveAll(rows);
        return distinctIds;
    }

    // 방문 기록의 태그 ID 목록 조회
    private List<Integer> findTagIds(Long visitId) {
        return visitTagRepository.findByVisitId(visitId).stream()
                .map(VisitTag::getTagId)
                .toList();
    }

    // 선호도 갱신 (생성/수정/삭제 공통 지점)
    // - user_preferences: 즉시 재집계 (flush는 updater 내부에서 수행)
    // - place_preferences: dirty 표시만. 실제 재집계는 추천 조회 시
    private void refreshPreferences(Integer placeId) {
        userPreferenceUpdater.refresh(TEMP_USER_ID);
        placePreferenceUpdater.markDirty(placeId);
    }

    // List -> Set 변환 (null 시 빈 집합)
    private Set<String> toSet(List<String> values) {
        return values == null ? new LinkedHashSet<>() : new LinkedHashSet<>(values);
    }

    // List 방어적 복사 (null 시 빈 리스트, 순서 유지)
    private List<String> toList(List<String> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }
}
