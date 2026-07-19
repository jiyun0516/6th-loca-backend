package gdg.hongik.loca.dto.visit;

import gdg.hongik.loca.entity.VisitRecord;

import java.time.OffsetDateTime;

// 방문 기록 응답 DTO
// - 목록/생성/수정 공통 사용
// - 장소 이름 포함
public record VisitResponse(
        Long visitId,
        Integer placeId,
        String placeName,
        Short rating,
        String content,
        OffsetDateTime visitedAt,
        OffsetDateTime createdAt
) {

    // 엔티티 + 장소 이름 -> 응답 변환
    public static VisitResponse of(VisitRecord record, String placeName) {
        return new VisitResponse(
                record.getVisitId(),
                record.getPlaceId(),
                placeName,
                record.getRating(),
                record.getContent(),
                record.getVisitedAt(),
                record.getCreatedAt()
        );
    }
}
