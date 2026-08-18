package gdg.hongik.loca.dto.review;

import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.enums.CompanionType;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

// 장소별 리뷰 응답 DTO
public record PlaceReviewResponse(
        Long reviewId,
        String title,
        String content,
        CompanionType companion,
        List<String> keywords,

        // 선택 태그 ID 목록 (visit_tags)
        List<Integer> tagIds,

        List<String> imageUrls,
        OffsetDateTime visitedAt,
        OffsetDateTime createdAt
) {
    public static PlaceReviewResponse from(VisitRecord record, List<Integer> tagIds) {
        return new PlaceReviewResponse(
                record.getVisitId(),
                record.getTitle(),
                record.getContent(),
                record.getCompanion(),
                new ArrayList<>(record.getKeywords()),
                tagIds,
                new ArrayList<>(record.getImageUrls()),
                record.getVisitedAt(),
                record.getCreatedAt()
        );
    }
}
