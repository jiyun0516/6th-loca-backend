package gdg.hongik.loca.exception;

/**
 * 이미 등록된 kakaoPlaceId로 장소를 생성하려 할 때 발생하는 예외.
 */
public class DuplicateKakaoPlaceIdException extends RuntimeException {

    public DuplicateKakaoPlaceIdException(String kakaoPlaceId) {
        super("이미 등록된 장소입니다. kakaoPlaceId=" + kakaoPlaceId);
    }
}
