package gdg.hongik.loca.dto.placelist;

import gdg.hongik.loca.entity.PlaceList;

import java.time.OffsetDateTime;

// 장소 목록 요약 응답 DTO
// - itemCount 는 **보이는 항목만** 셈. 전체를 세면 들어갔을 때 개수가 어긋남
public record PlaceListResponse(
        Long listId,
        String name,
        int itemCount,
        OffsetDateTime createdAt
) {
    public static PlaceListResponse of(PlaceList list, int itemCount) {
        return new PlaceListResponse(
                list.getListId(),
                list.getName(),
                itemCount,
                list.getCreatedAt()
        );
    }
}
