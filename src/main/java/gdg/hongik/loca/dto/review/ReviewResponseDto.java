package gdg.hongik.loca.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.time.LocalDateTime;

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
}

