package gdg.hongik.loca.exception;

/**
 * 요청한 태그를 찾을 수 없을 때 발생하는 예외.
 */
public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(Integer tagId) {
        super("태그를 찾을 수 없습니다. tagId=" + tagId);
    }
}
