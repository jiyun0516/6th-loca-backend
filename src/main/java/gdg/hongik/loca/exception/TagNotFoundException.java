package gdg.hongik.loca.exception;

import java.util.Collection;

// 요청한 태그를 찾을 수 없을 때 발생하는 예외
public class TagNotFoundException extends RuntimeException {

    // 단일 태그 검증 경로
    public TagNotFoundException(Integer tagId) {
        super("태그를 찾을 수 없습니다. tagId=" + tagId);
    }

    // 여러 태그 검증 경로
    public TagNotFoundException(Collection<Integer> tagIds) {
        super("태그를 찾을 수 없습니다. tagIds=" + tagIds);
    }
}
