package gdg.hongik.loca.dto.placelist;

import jakarta.validation.constraints.NotNull;

// 목록 항목 담기 요청 DTO
public record PlaceListItemCreateRequest(
        @NotNull(message = "장소 ID(placeId)는 필수입니다.")
        Integer placeId
) {
}
