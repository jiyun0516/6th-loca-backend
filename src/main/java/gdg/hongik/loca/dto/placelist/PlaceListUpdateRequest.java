package gdg.hongik.loca.dto.placelist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 장소 목록 수정 요청 DTO (이름만)
public record PlaceListUpdateRequest(
        @NotBlank(message = "목록 이름은 필수입니다.")
        @Size(max = 50, message = "목록 이름은 50자 이하여야 합니다.")
        String name
) {
}
