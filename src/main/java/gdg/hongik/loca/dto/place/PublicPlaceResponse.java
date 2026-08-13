package gdg.hongik.loca.dto.place;

import gdg.hongik.loca.entity.PublicPlace;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 장소 응답 DTO.
 */
public record PublicPlaceResponse(
        Integer placeId,
        String name,
        String kakaoPlaceId,
        String address,
        BigDecimal lat,
        BigDecimal lng,
        OffsetDateTime createdAt
) {

    public static PublicPlaceResponse from(PublicPlace place) {
        return new PublicPlaceResponse(
                place.getPlaceId(),
                place.getName(),
                place.getKakaoPlaceId(),
                place.getAddress(),
                place.getLat(),
                place.getLng(),
                place.getCreatedAt()
        );
    }
}
