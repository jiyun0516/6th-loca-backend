package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.review.ReviewCreateRequestDto;
import gdg.hongik.loca.dto.review.ReviewResponseDto;
import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.exception.VisitRecordNotFoundException;
import gdg.hongik.loca.repository.VisitRecordRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// 방문 후기(리뷰) 도메인 서비스 계층
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final VisitRecordRepository visitRecordRepository;

    // 임시 userId, JWT 도입 시 토큰에서 추출로 교체
    private static final Integer TEMP_USER_ID = 1;

    // 방문 후기 생성
    // - VisitRecord 저장 후 응답 DTO 반환(201)
    // - visitedAt 미수신 시 기록 시각으로 대체
    @Transactional
    public ReviewResponseDto create(@Valid ReviewCreateRequestDto request) {
        VisitRecord record = VisitRecord.builder()
                .userId(TEMP_USER_ID)
                .placeId(request.getPlaceId())
                .title(request.getTitle())
                .rating(toShort(request.getRating()))
                .companion(request.getCompanion())
                .mood(request.getMood())
                .memorableMoment(request.getMemorableMoment())
                .goodPoint(request.getGoodPoint())
                .price(request.getPrice())
                .unknownPrice(request.getUnknownPrice())
                .messageToFuture(request.getMessageToFuture())
                .keywords(toSet(request.getKeywords()))
                .atmosphereTags(toSet(request.getAtmosphereTags()))
                .imageUrls(toList(request.getImageUrls()))
                .visitedAt(OffsetDateTime.now()) // 방문 일시 미수신 시 기록 시각으로 대체
                .build();
        return ReviewResponseDto.from(visitRecordRepository.save(record));
    }

    // 내 방문 후기 목록
    // - deletedAt null, visitedAt 내림차순
    public List<ReviewResponseDto> list() {
        return visitRecordRepository.findActiveByUserId(TEMP_USER_ID).stream()
                .map(ReviewResponseDto::from)
                .toList();
    }

    // 내 방문 후기 상세
    // - 키워드/분위기태그/이미지 포함
    public ReviewResponseDto detail(Long visitId) {
        return ReviewResponseDto.from(findActiveOwned(visitId));
    }

    // 내 방문 후기 수정
    // - dirty checking
    // - 키워드/분위기태그/이미지 컬렉션은 전체 교체
    @Transactional
    public ReviewResponseDto update(Long visitId, @Valid ReviewCreateRequestDto request) {
        VisitRecord record = findActiveOwned(visitId);
        record.setTitle(request.getTitle());
        record.setRating(toShort(request.getRating()));
        record.setCompanion(request.getCompanion());
        record.setMood(request.getMood());
        record.setMemorableMoment(request.getMemorableMoment());
        record.setGoodPoint(request.getGoodPoint());
        record.setPrice(request.getPrice());
        record.setUnknownPrice(request.getUnknownPrice());
        record.setMessageToFuture(request.getMessageToFuture());
        record.setKeywords(toSet(request.getKeywords()));
        record.setAtmosphereTags(toSet(request.getAtmosphereTags()));
        record.setImageUrls(toList(request.getImageUrls()));
        return ReviewResponseDto.from(record);
    }

    // 내 방문 후기 삭제(soft-delete)
    // - deletedAt = now
    @Transactional
    public void delete(Long visitId) {
        findActiveOwned(visitId).setDeletedAt(OffsetDateTime.now());
    }

    // 활성 + 소유 단건 조회 헬퍼
    // - 미존재/삭제됨/소유자 불일치 -> VisitRecordNotFoundException
    private VisitRecord findActiveOwned(Long visitId) {
        return visitRecordRepository.findActiveByVisitIdAndUserId(visitId, TEMP_USER_ID)
                .orElseThrow(() -> new VisitRecordNotFoundException(visitId));
    }

    // Integer 만족도 -> 엔티티 Short 변환 (null 허용)
    private Short toShort(Integer value) {
        return value == null ? null : value.shortValue();
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
