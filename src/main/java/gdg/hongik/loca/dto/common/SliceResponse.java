package gdg.hongik.loca.dto.common;

import org.springframework.data.domain.Slice;

import java.util.List;

// 슬라이스(더보기/무한 스크롤) 공통 응답 DTO
// - hasNext : 다음 페이지 존재 여부. false 면 프론트가 추가 로딩을 멈춤
public record SliceResponse<T>(
        List<T> content,
        int page,
        int size,
        boolean hasNext
) {
    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext()
        );
    }
}
