package gdg.hongik.loca.dto.tag;

import jakarta.validation.constraints.NotBlank;

/**
 * 태그 생성 요청 DTO.
 */
public record TagCreateRequest(

        @NotBlank(message = "태그 이름은 필수입니다.")
        String name
) {
}
