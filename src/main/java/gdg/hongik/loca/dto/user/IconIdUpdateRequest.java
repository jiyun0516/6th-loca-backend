package gdg.hongik.loca.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record IconIdUpdateRequest(
        @NotNull(message = "iconId는 필수입니다.")
        @Min(value = 1, message = "iconId는 1 이상이어야 합니다.")
        Integer iconId
) {
}