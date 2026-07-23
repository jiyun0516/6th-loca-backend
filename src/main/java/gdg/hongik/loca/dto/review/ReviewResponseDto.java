package gdg.hongik.loca.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.enums.CompanionType;

@Getter
@AllArgsConstructor
@Builder
public class ReviewResponseDto {

    private Long reviewId;

    private Long placeId;

    private String title;

    private CompanionType companion;

    private List<String> keywords;

    private List<String> atmosphereTags;

    private List<String> imageUrls;

    private LocalDateTime createdAt;

    // 엔티티 -> 응답 DTO 매핑
    // - reviewId <- visitId
    // - placeId(Long) <- 엔티티 placeId(Integer)
    // - keywords/atmosphereTags: Set -> List
    // - createdAt(LocalDateTime) <- 엔티티 createdAt(OffsetDateTime)
    public static ReviewResponseDto from(VisitRecord record) {
        return ReviewResponseDto.builder()
                .reviewId(record.getVisitId())
                .placeId(record.getPlaceId() == null ? null : record.getPlaceId().longValue())
                .title(record.getTitle())
                .companion(record.getCompanion())
                .keywords(record.getKeywords() == null ? null : new ArrayList<>(record.getKeywords()))
                .atmosphereTags(record.getAtmosphereTags() == null ? null : new ArrayList<>(record.getAtmosphereTags()))
                .imageUrls(record.getImageUrls() == null ? null : new ArrayList<>(record.getImageUrls()))
                .createdAt(record.getCreatedAt() == null ? null : record.getCreatedAt().toLocalDateTime())
                .build();
    }
}

