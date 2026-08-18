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
    // PublicPlace -> 응답 DTO
    // - 점수는 내보내지 않음 (정렬에만 쓰고 화면에 노출하지 않음)
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
