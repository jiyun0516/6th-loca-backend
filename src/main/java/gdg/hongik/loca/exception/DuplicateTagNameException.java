package gdg.hongik.loca.exception;

/**
 * 이미 존재하는 이름으로 태그를 생성하려 할 때 발생하는 예외.
 */
public class DuplicateTagNameException extends RuntimeException {

    public DuplicateTagNameException(String name) {
        super("이미 존재하는 태그입니다. name=" + name);
    }
}
