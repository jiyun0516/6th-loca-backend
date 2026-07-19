package gdg.hongik.loca.dto.visit;

import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.entity.VisitRecord;

import java.time.OffsetDateTime;
import java.util.List;

// 방문 기록 상세 응답 DTO
// - 기본 정보 + 장소 이름 + 태그 목록
public record VisitDetailResponse(
        Long visitId,
        Integer placeId,
        String placeName,
        Short rating,
        String content,
        OffsetDateTime visitedAt,
        List<TagResponse> tags,
        OffsetDateTime createdAt
) {

    // 엔티티 + 장소 이름 + 태그 목록 -> 상세 응답 변환
    public static VisitDetailResponse of(VisitRecord record, String placeName, List<TagResponse> tags) {
        return new VisitDetailResponse(
                record.getVisitId(),
                record.getPlaceId(),
                placeName,
                record.getRating(),
                record.getContent(),
                record.getVisitedAt(),
                tags,
                record.getCreatedAt()
        );
    }
}
