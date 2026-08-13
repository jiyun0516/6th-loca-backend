package gdg.hongik.loca.dto.placelist;

import gdg.hongik.loca.entity.PlaceList;

import java.time.OffsetDateTime;
import java.util.List;

// 장소 목록 상세 응답 DTO
// - hiddenCount = 삭제되었거나 공개가 꺼져 보이지 않는 항목 수
//   소유자에게만 노출함. 20개 담았는데 18개만 보이는 이유는 소유자가 알아야 함
//   공유 링크 조회에서는 개수조차 내보내지 않음 (공유 PR)
public record PlaceListDetailResponse(
        Long listId,
        String name,
        List<PlaceListItemResponse> items,
        int hiddenCount,
        OffsetDateTime createdAt
) {
    public static PlaceListDetailResponse of(PlaceList list, List<PlaceListItemResponse> items, int hiddenCount) {
        return new PlaceListDetailResponse(
                list.getListId(),
                list.getName(),
                items,
                hiddenCount,
                list.getCreatedAt()
        );
    }
}
