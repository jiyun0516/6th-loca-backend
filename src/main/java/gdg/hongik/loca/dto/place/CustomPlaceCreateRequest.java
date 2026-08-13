package gdg.hongik.loca.dto.place;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// 사용자 생성 장소 생성 요청 DTO
public record CustomPlaceCreateRequest(
        @NotBlank(message = "장소 이름은 필수입니다.")
        String name,

        String address,

        @NotNull(message = "위도(lat)는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        BigDecimal lat,

        @NotNull(message = "경도(lng)는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        BigDecimal lng,

        // 서버 기본값을 두지 않음. 값이 없으면 사용자가 공개 여부를 고른 적이 없다는 뜻
        @NotNull(message = "공개 여부(isShareable)는 필수입니다.")
        Boolean isShareable
) {
}
