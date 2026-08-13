package gdg.hongik.loca.dto.placelist;

import gdg.hongik.loca.entity.CustomPlace;
import gdg.hongik.loca.entity.PublicPlace;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

// 목록 항목 응답 DTO
// - CATALOG(public)와 CUSTOM 이 한 배열에 섞이므로 타입별 DTO 로 나누지 않고 placeType 으로 구분
// - CUSTOM 은 kakaoPlaceId 가 없어 항상 null. 프론트는 좌표로 핀을 찍어야 함
public record PlaceListItemResponse(
        Integer placeId,
        String placeType,
        String name,
        String address,
        BigDecimal lat,
        BigDecimal lng,
        String kakaoPlaceId,
        OffsetDateTime addedAt
) {
    public static PlaceListItemResponse of(PublicPlace place, OffsetDateTime addedAt) {
        return new PlaceListItemResponse(
                place.getPlaceId(),
                "PUBLIC",
                place.getName(),
                place.getAddress(),
                place.getLat(),
                place.getLng(),
                place.getKakaoPlaceId(),
                addedAt
        );
    }

    public static PlaceListItemResponse of(CustomPlace place, OffsetDateTime addedAt) {
        return new PlaceListItemResponse(
                place.getPlaceId(),
                "CUSTOM",
                place.getName(),
                place.getAddress(),
                place.getLat(),
                place.getLng(),
                null,
                addedAt
        );
    }
}
