package gdg.hongik.loca.dto.place;

import gdg.hongik.loca.entity.CustomPlace;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

// 사용자 생성 장소 응답 DTO
public record CustomPlaceResponse(
        Integer placeId,
        Integer userId,
        String name,
        String address,
        BigDecimal lat,
        BigDecimal lng,
        Boolean isShareable,
        OffsetDateTime createdAt
) {
    public static CustomPlaceResponse from(CustomPlace place) {
        return new CustomPlaceResponse(
                place.getPlaceId(),
                place.getUserId(),
                place.getName(),
                place.getAddress(),
                place.getLat(),
                place.getLng(),
                place.getIsShareable(),
                place.getCreatedAt()
        );
    }
}
