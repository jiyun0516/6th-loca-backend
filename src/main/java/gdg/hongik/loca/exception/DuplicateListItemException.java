package gdg.hongik.loca.exception;

// 복합 PK 가 DB 에서 막지만, 폴백 400 보다 409 가 맞는 응답이라 선검사함
public class DuplicateListItemException extends RuntimeException {
    public DuplicateListItemException(Long listId, Integer placeId) {
        super("이미 목록에 담긴 장소입니다. listId=" + listId + ", placeId=" + placeId);
    }
}
