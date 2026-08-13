package gdg.hongik.loca.dto.placelist;

import gdg.hongik.loca.entity.PlaceList;

import java.time.OffsetDateTime;

// 공유 토큰 응답 DTO
// - shareUrl 은 서버가 만들지 않음. 프론트 도메인이 미정이라 조립하면 환경 종속이 생김
public record ShareResponse(
        String shareToken,
        OffsetDateTime sharedAt
) {
    public static ShareResponse from(PlaceList list) {
        return new ShareResponse(list.getShareToken(), list.getSharedAt());
    }
}
