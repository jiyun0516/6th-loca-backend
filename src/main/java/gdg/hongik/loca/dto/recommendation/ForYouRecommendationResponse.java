package gdg.hongik.loca.dto.recommendation;

import gdg.hongik.loca.entity.PublicPlace;
import java.math.BigDecimal;

// ForYou 추천 장소 응답 DTO
public record ForYouRecommendationResponse(
        Integer placeId,
        String name,
        String address,
        BigDecimal lat,
        BigDecimal lng
) {

    public static ForYouRecommendationResponse from(
            PublicPlace place
    ) {
        return new ForYouRecommendationResponse(
                place.getPlaceId(),
                place.getName(),
                place.getAddress(),
                place.getLat(),
                place.getLng()
        );
    }
}