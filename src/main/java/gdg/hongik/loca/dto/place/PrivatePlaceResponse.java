package gdg.hongik.loca.dto.place;

import gdg.hongik.loca.entity.PrivatePlace;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

// 개인 장소 응답 DTO
public record PrivatePlaceResponse(
        Integer placeId,
        Integer userId,
        String name,
        String address,
        BigDecimal lat,
        BigDecimal lng,
        OffsetDateTime createdAt
) {
    public static PrivatePlaceResponse from(PrivatePlace place) {
        return new PrivatePlaceResponse(
                place.getPlaceId(),
                place.getUserId(),
                place.getName(),
                place.getAddress(),
                place.getLat(),
                place.getLng(),
                place.getCreatedAt()
        );
    }
}
