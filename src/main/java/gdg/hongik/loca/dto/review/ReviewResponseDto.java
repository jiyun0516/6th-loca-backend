package gdg.hongik.loca.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;

import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.enums.CompanionType;

@Getter
@AllArgsConstructor
@Builder
public class ReviewResponseDto {

    private Long reviewId;

    private Integer placeId;

    private String title;

    private String content;

    private CompanionType companion;

    private List<String> keywords;

    // 선택 태그 ID 목록 (visit_tags)
    private List<Integer> tagIds;

    private List<String> imageUrls;

    private OffsetDateTime visitedAt;

    private OffsetDateTime createdAt;

    // 엔티티 -> 응답 DTO 매핑
    // - reviewId <- visitId
    // - placeId: 엔티티와 동일 Integer (변환 없음)
    // - keywords: Set -> List
    // - tagIds: visit_tags 조회 결과를 서비스에서 주입 (엔티티 연관 매핑 없음)
    // - visitedAt/createdAt: OffsetDateTime 그대로 (타임존 보존)
    public static ReviewResponseDto from(VisitRecord record, List<Integer> tagIds) {
        return ReviewResponseDto.builder()
                .reviewId(record.getVisitId())
                .placeId(record.getPlaceId())
                .title(record.getTitle())
                .content(record.getContent())
                .companion(record.getCompanion())
                .keywords(record.getKeywords() == null ? null : new ArrayList<>(record.getKeywords()))
                .tagIds(tagIds == null ? List.of() : tagIds)
                .imageUrls(record.getImageUrls() == null ? null : new ArrayList<>(record.getImageUrls()))
                .visitedAt(record.getVisitedAt())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
