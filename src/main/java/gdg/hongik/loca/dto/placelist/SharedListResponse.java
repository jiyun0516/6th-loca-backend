package gdg.hongik.loca.dto.placelist;

import gdg.hongik.loca.entity.PlaceList;

import java.time.OffsetDateTime;
import java.util.List;

// 공유 링크 조회 응답 DTO
// - listId 를 내보내지 않음. serial 이라 노출하면 다른 목록을 유추할 근거가 됨
// - 소유자 정보도 내보내지 않음 (signup 이 nickname 을 안 받아 전부 null 이기도 함)
// - hiddenCount 없음. 보이지 않는 항목은 **개수조차** 제3자에게 알리지 않음
public record SharedListResponse(
        String name,
        List<PlaceListItemResponse> items,
        OffsetDateTime createdAt
) {
    public static SharedListResponse of(PlaceList list, List<PlaceListItemResponse> items) {
        return new SharedListResponse(list.getName(), items, list.getCreatedAt());
    }
}
