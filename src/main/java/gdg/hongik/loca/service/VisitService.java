package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.dto.visit.VisitCreateRequest;
import gdg.hongik.loca.dto.visit.VisitDetailResponse;
import gdg.hongik.loca.dto.visit.VisitResponse;
import gdg.hongik.loca.dto.visit.VisitUpdateRequest;
import gdg.hongik.loca.entity.Place;
import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.entity.VisitTag;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.exception.TagNotFoundException;
import gdg.hongik.loca.exception.VisitRecordNotFoundException;
import gdg.hongik.loca.repository.PlaceRepository;
import gdg.hongik.loca.repository.TagRepository;
import gdg.hongik.loca.repository.VisitRecordRepository;
import gdg.hongik.loca.repository.VisitTagRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 방문 기록 도메인 서비스 계층
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitService {

    private final VisitRecordRepository visitRecordRepository;
    private final VisitTagRepository visitTagRepository;
    private final PlaceRepository placeRepository;
    private final TagRepository tagRepository;

    // 방문 기록 생성
    // - 장소 미존재 -> PlaceNotFoundException
    // - 태그 미존재 -> TagNotFoundException
    // - VisitRecord 저장 후 tagIds로 VisitTag 저장
    @Transactional
    public VisitResponse create(Integer userId, @Valid VisitCreateRequest request) {
        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new PlaceNotFoundException(request.placeId()));
        validateTagsExist(request.tagIds());

        VisitRecord record = VisitRecord.builder()
                .userId(userId)
                .placeId(request.placeId())
                .rating(request.rating())
                .content(request.content())
                .visitedAt(request.visitedAt())
                .build();
        VisitRecord saved = visitRecordRepository.save(record);

        saveVisitTags(saved.getVisitId(), request.tagIds());

        return VisitResponse.of(saved, place.getName());
    }

    // 내 방문 기록 목록 조회
    // - deletedAt null, visitedAt 내림차순
    // - 각 항목에 장소 이름 포함
    public List<VisitResponse> getMyVisits(Integer userId) {
        List<VisitRecord> records =
                visitRecordRepository.findByUserIdAndDeletedAtIsNullOrderByVisitedAtDesc(userId);
        Map<Integer, String> placeNames = getPlaceNames(records);
        return records.stream()
                .map(record -> VisitResponse.of(record, placeNames.get(record.getPlaceId())))
                .toList();
    }

    // 내 방문 기록 상세 조회
    // - 태그 이름 목록 포함
    public VisitDetailResponse getMyVisit(Integer userId, Long visitId) {
        VisitRecord record = findActiveOwned(userId, visitId);
        String placeName = getPlaceName(record.getPlaceId());
        return VisitDetailResponse.of(record, placeName, getVisitTags(visitId));
    }

    // 내 방문 기록 수정
    // - rating/content/visitedAt 변경 감지 반영
    // - 태그 전체 교체(기존 삭제 후 재저장)
    @Transactional
    public VisitResponse updateMyVisit(Integer userId, Long visitId, @Valid VisitUpdateRequest request) {
        VisitRecord record = findActiveOwned(userId, visitId);
        validateTagsExist(request.tagIds());

        record.setRating(request.rating());
        record.setContent(request.content());
        record.setVisitedAt(request.visitedAt());

        visitTagRepository.deleteByVisitId(visitId);
        saveVisitTags(visitId, request.tagIds());

        return VisitResponse.of(record, getPlaceName(record.getPlaceId()));
    }

    // 내 방문 기록 삭제(soft-delete)
    // - deletedAt = now
    // - VisitTag는 유지
    @Transactional
    public void deleteMyVisit(Integer userId, Long visitId) {
        VisitRecord record = findActiveOwned(userId, visitId);
        record.setDeletedAt(OffsetDateTime.now());
    }

    // 활성 + 소유 방문 기록 조회 헬퍼
    // - 미존재/삭제됨/소유자 불일치 -> VisitRecordNotFoundException
    private VisitRecord findActiveOwned(Integer userId, Long visitId) {
        return visitRecordRepository.findByVisitIdAndUserIdAndDeletedAtIsNull(visitId, userId)
                .orElseThrow(() -> new VisitRecordNotFoundException(visitId));
    }

    // 태그 존재 검증
    // - 미존재 tagId -> TagNotFoundException
    private void validateTagsExist(List<Integer> tagIds) {
        if (tagIds == null) {
            return;
        }
        for (Integer tagId : tagIds) {
            if (!tagRepository.existsById(tagId)) {
                throw new TagNotFoundException(tagId);
            }
        }
    }

    // VisitTag 저장
    // - tagIds 없으면 스킵
    // - 중복 tagId 제거
    private void saveVisitTags(Long visitId, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<VisitTag> visitTags = tagIds.stream()
                .distinct()
                .map(tagId -> VisitTag.builder().visitId(visitId).tagId(tagId).build())
                .toList();
        visitTagRepository.saveAll(visitTags);
    }

    // 방문 기록의 태그 목록 조회
    // - visit_tags -> tags 조인
    private List<TagResponse> getVisitTags(Long visitId) {
        List<Integer> tagIds = visitTagRepository.findByVisitId(visitId).stream()
                .map(VisitTag::getTagId)
                .toList();
        return tagRepository.findAllById(tagIds).stream()
                .map(TagResponse::from)
                .toList();
    }

    // 단건 장소 이름 조회
    private String getPlaceName(Integer placeId) {
        return placeRepository.findById(placeId)
                .map(Place::getName)
                .orElse(null);
    }

    // 목록의 장소 이름 매핑 조회
    // - placeId -> name
    private Map<Integer, String> getPlaceNames(List<VisitRecord> records) {
        List<Integer> placeIds = records.stream()
                .map(VisitRecord::getPlaceId)
                .distinct()
                .toList();
        return placeRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(Place::getPlaceId, Place::getName));
    }
}
