package gdg.hongik.loca.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.enums.CompanionType;
import gdg.hongik.loca.enums.MoodType;

@Getter
@AllArgsConstructor
@Builder
public class ReviewResponseDto {

    private Long reviewId;

    private Long placeId;

    private String title;

    private CompanionType companion;

    private MoodType mood;

    private List<String> keywords;

    private String memorableMoment;

    private String goodPoint;

    private Integer rating;

    private Integer price;

    private Boolean unknownPrice;

    private List<String> atmosphereTags;

    private String messageToFuture;

    private List<String> imageUrls;

    private LocalDateTime createdAt;

    // 엔티티 -> 응답 DTO 매핑
    // - reviewId <- visitId
    // - placeId(Long) <- 엔티티 placeId(Integer)
    // - rating(Integer) <- 엔티티 rating(Short), null 허용
    // - keywords/atmosphereTags: Set -> List
    // - createdAt(LocalDateTime) <- 엔티티 createdAt(OffsetDateTime)
    public static ReviewResponseDto from(VisitRecord record) {
        return ReviewResponseDto.builder()
                .reviewId(record.getVisitId())
                .placeId(record.getPlaceId() == null ? null : record.getPlaceId().longValue())
                .title(record.getTitle())
                .companion(record.getCompanion())
                .mood(record.getMood())
                .keywords(record.getKeywords() == null ? null : new ArrayList<>(record.getKeywords()))
                .memorableMoment(record.getMemorableMoment())
                .goodPoint(record.getGoodPoint())
                .rating(record.getRating() == null ? null : record.getRating().intValue())
                .price(record.getPrice())
                .unknownPrice(record.getUnknownPrice())
                .atmosphereTags(record.getAtmosphereTags() == null ? null : new ArrayList<>(record.getAtmosphereTags()))
                .messageToFuture(record.getMessageToFuture())
                .imageUrls(record.getImageUrls() == null ? null : new ArrayList<>(record.getImageUrls()))
                .createdAt(record.getCreatedAt() == null ? null : record.getCreatedAt().toLocalDateTime())
                .build();
    }
}

