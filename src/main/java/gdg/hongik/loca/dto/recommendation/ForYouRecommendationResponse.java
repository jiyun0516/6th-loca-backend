package gdg.hongik.loca.dto.recommendation;

import gdg.hongik.loca.entity.PublicPlace;

// ForYou 추천 장소 응답 DTO
public record ForYouRecommendationResponse(
        Integer placeId,
        String name,
        String address,
        String recommendationReason
) {

    public static ForYouRecommendationResponse of(
            PublicPlace place,
            String recommendationReason
    ) {
        return new ForYouRecommendationResponse(
                place.getPlaceId(),
                place.getName(),
                place.getAddress(),
                recommendationReason
        );
    }
}