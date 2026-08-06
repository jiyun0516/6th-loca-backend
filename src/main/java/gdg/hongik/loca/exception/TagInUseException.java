package gdg.hongik.loca.exception;

// 리뷰에 사용 중인 태그를 삭제하려 할 때 발생하는 예외
public class TagInUseException extends RuntimeException {

    public TagInUseException(Integer tagId) {
        super("리뷰에 사용 중인 태그는 삭제할 수 없습니다. tagId=" + tagId);
    }
}
