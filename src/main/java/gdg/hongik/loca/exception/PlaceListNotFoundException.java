package gdg.hongik.loca.exception;

// 목록 미존재 / 타인 소유 모두 이 예외로 통일
// - 타인 소유를 403 으로 구분하면 "그런 목록이 있긴 하다"를 흘림
public class PlaceListNotFoundException extends RuntimeException {
    public PlaceListNotFoundException(Long listId) {
        super("목록을 찾을 수 없습니다. listId=" + listId);
    }
}
