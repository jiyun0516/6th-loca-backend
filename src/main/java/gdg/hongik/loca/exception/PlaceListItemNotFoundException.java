package gdg.hongik.loca.exception;

public class PlaceListItemNotFoundException extends RuntimeException {
    public PlaceListItemNotFoundException(Long listId, Integer placeId) {
        super("목록에 담긴 장소를 찾을 수 없습니다. listId=" + listId + ", placeId=" + placeId);
    }
}
