package gdg.hongik.loca.dto.place;

import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.entity.Place;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

// 장소 상세 응답 DTO
// - 기본 정보 + 태그 목록 + 평균 평점 + 방문 수
public record PlaceDetailResponse(
        Integer placeId,
        String name,
        String kakaoPlaceId,
        String address,
        BigDecimal lat,
        BigDecimal lng,
        List<TagResponse> tags,
        Double averageRating,
        long visitCount,
        OffsetDateTime createdAt
) {

    // 조립 팩토리
    // - place: 장소 엔티티
    // - tags: 매핑된 태그 목록
    // - averageRating: 평균 평점(기록 없으면 null)
    // - visitCount: 유효 방문 수
    public static PlaceDetailResponse of(
            Place place,
            List<TagResponse> tags,
            Double averageRating,
            long visitCount
    ) {
        return new PlaceDetailResponse(
                place.getPlaceId(),
                place.getName(),
                place.getKakaoPlaceId(),
                place.getAddress(),
                place.getLat(),
                place.getLng(),
                tags,
                averageRating,
                visitCount,
                place.getCreatedAt()
        );
    }
}
