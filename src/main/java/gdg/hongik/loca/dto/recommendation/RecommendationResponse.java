package gdg.hongik.loca.dto.recommendation;

import gdg.hongik.loca.entity.PublicPlace;

import java.math.BigDecimal;

// 추천 장소 응답 DTO
public record RecommendationResponse(
        Integer placeId,
        String name,
        String kakaoPlaceId,
        String address,
        BigDecimal lat,
        BigDecimal lng
) {
    // PublicPlace + 매칭 점수 -> 응답 DTO
    public static RecommendationResponse of(PublicPlace place) {
        return new RecommendationResponse(
                place.getPlaceId(),
                place.getName(),
                place.getKakaoPlaceId(),
                place.getAddress(),
                place.getLat(),
                place.getLng()
        );
    }
}
