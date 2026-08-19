package gdg.hongik.loca.dto.place;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 장소 생성 요청 DTO.
 */
public record PublicPlaceCreateRequest(

        @NotBlank(message = "장소 이름은 필수입니다.")
        String name,

        @NotBlank(message = "kakaoPlaceId는 필수입니다.")
        String kakaoPlaceId,

        String address,

        @NotNull(message = "위도(lat)는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        BigDecimal lat,

        @NotNull(message = "경도(lng)는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        BigDecimal lng,

        String imageUrl
) {
}
