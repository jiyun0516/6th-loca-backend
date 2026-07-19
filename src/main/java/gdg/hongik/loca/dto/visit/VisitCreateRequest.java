package gdg.hongik.loca.dto.visit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

// 방문 기록 생성 요청 DTO
public record VisitCreateRequest(

        @NotNull(message = "placeId는 필수입니다.")
        Integer placeId,

        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        Short rating,

        String content,

        @NotNull(message = "방문 일시(visitedAt)는 필수입니다.")
        OffsetDateTime visitedAt,

        List<Integer> tagIds
) {
}
