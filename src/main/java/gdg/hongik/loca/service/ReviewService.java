package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.review.ReviewCreateRequest;
import gdg.hongik.loca.dto.review.ReviewResponse;
import gdg.hongik.loca.dto.review.ReviewUpdateRequest;
import gdg.hongik.loca.entity.Tag;
import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.entity.VisitTag;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.exception.TagNotFoundException;
import gdg.hongik.loca.exception.VisitRecordNotFoundException;
import gdg.hongik.loca.repository.PublicPlaceRepository;
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
import java.util.Map;
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
    private final PublicPlaceRepository publicPlaceRepository;
    private final UserPreferenceUpdater userPreferenceUpdater;
    private final PlacePreferenceUpdater placePreferenceUpdater;



    // 방문 후기 생성
    // - 순서 : 장소 검증 -> 리뷰 저장 -> 리뷰 태그 저장 -> 선호도 갱신
    // - visitedAt 미수신 시 기록 시각으로 대체
    @Transactional
    public ReviewResponse create(Integer userId, ReviewCreateRequest request) {
        // 장소가 존재하지 않거나 삭제된 장소일 시 PlaceNotFoundException 발생
        if (!publicPlaceRepository.existsByPlaceIdAndDeletedAtIsNull(request.placeId())) {
            throw new PlaceNotFoundException(request.placeId());
        }

        VisitRecord record = VisitRecord.builder()
                .userId(userId)
                .placeId(request.placeId())
                .title(request.title())
                .content(request.content())
                .companion(request.companion())
                .keywords(toSet(request.keywords()))
                .imageUrls(toList(request.imageUrls()))
                .visitedAt(request.visitedAt() == null ? OffsetDateTime.now() : request.visitedAt())
                .build();
        VisitRecord saved = visitRecordRepository.save(record);

        List<Integer> tagIds = saveTags(saved.getVisitId(), request.tagIds());
        refreshPreferences(saved.getPlaceId());

        return ReviewResponse.from(saved, tagIds);
    }

    // 내 방문 후기 목록
    // - visitedAt 내림차순
    public List<ReviewResponse> list(Integer userId) {
        List<VisitRecord> records = visitRecordRepository.findByUserIdOrderByVisitedAtDesc(userId);
        if (records.isEmpty()) {
            return List.of();
        }

        List<Long> visitIds = records.stream()
                .map(VisitRecord::getVisitId)
                .toList();

        Map<Long, List<Integer>> tagIdsByVisitId = visitTagRepository.findByVisitIdIn(visitIds).stream()
                .collect(Collectors.groupingBy(
                        VisitTag::getVisitId,
                        Collectors.mapping(VisitTag::getTagId, Collectors.toList())));

        return records.stream()
                .map(r -> ReviewResponse.from(r, tagIdsByVisitId.getOrDefault(r.getVisitId(), List.of())))
                .toList();
    }

    // 내 방문 후기 상세
    // - 키워드/태그/이미지 포함
    public ReviewResponse detail(Integer userId, Long visitId) {
        VisitRecord record = findOwned(visitId, userId);
        return ReviewResponse.from(record, findTagIds(visitId));
    }

    // 내 방문 후기 수정
    // - 모든 필드는 null이면 기존 값 유지
    // - 컬렉션은 null이면 유지, []이면 전체 삭제
    // - 컬렉션은 참조를 바꾸지 않고 내용만 교체
    @Transactional
    public ReviewResponse update(Integer userId, Long visitId, ReviewUpdateRequest request) {
        VisitRecord record = findOwned(visitId, userId);

        // 필드별 업데이트
        if (request.title() != null) {
            record.setTitle(request.title());
        }
        if (request.content() != null) {
            record.setContent(request.content());
        }
        if (request.companion() != null) {
            record.setCompanion(request.companion());
        }
        if (request.visitedAt() != null) {
            record.setVisitedAt(request.visitedAt());
        }
        if (request.keywords() != null) {
            record.getKeywords().clear();
            record.getKeywords().addAll(request.keywords());
        }
        if (request.imageUrls() != null) {
            record.getImageUrls().clear();
            record.getImageUrls().addAll(request.imageUrls());
        }

        // 태그가 그대로면 선호도 값이 변할 수 없으므로 재집계를 건너뜀
        List<Integer> tagIds;
        if (request.tagIds() == null) {
            tagIds = findTagIds(visitId);
        } else {
            visitTagRepository.deleteByVisitId(visitId);
            tagIds = saveTags(visitId, request.tagIds());
            refreshPreferences(record.getPlaceId());
        }

        return ReviewResponse.from(record, tagIds);
    }

    // 내 방문 후기 삭제(소프트 삭제)
    @Transactional
    public void delete(Integer userId, Long visitId) {
        VisitRecord record = findOwned(visitId, userId);
        Integer placeId = record.getPlaceId();

        visitTagRepository.deleteByVisitId(visitId);
        visitRecordRepository.delete(record);
        refreshPreferences(placeId);
    }

    // 리뷰 소유 단건 조회
    // - visitId가 존재하지 않거나 소유자가 불일치할 시 VisitRecordNotFoundException 발생
    private VisitRecord findOwned(Long visitId, Integer userId) {
        return visitRecordRepository
                .findByVisitIdAndUserId(visitId, userId)
                .orElseThrow(() -> new VisitRecordNotFoundException(visitId));
    }

    // 선택 태그 저장
    // - 같은 리뷰 안의 중복 tagId는 하나로 합침
    // - tagId가 존재하지 않을 시 TagNotFoundException 발생
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
        userPreferenceUpdater.refresh(placeId);
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
