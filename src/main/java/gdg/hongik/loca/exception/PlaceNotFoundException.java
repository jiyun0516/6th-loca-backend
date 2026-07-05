package gdg.hongik.loca.exception;

/**
 * 요청한 장소를 찾을 수 없을 때 발생하는 예외.
 */
public class PlaceNotFoundException extends RuntimeException {

    public PlaceNotFoundException(Integer placeId) {
        super("장소를 찾을 수 없습니다. placeId=" + placeId);
    }
}
