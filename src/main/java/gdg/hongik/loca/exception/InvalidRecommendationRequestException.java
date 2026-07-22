package gdg.hongik.loca.exception;

// 추천 요청 파라미터가 유효하지 않을 때 발생하는 예외
// - tagIds 미선택(빈 값) 등 잘못된 요청 공통 사용
public class InvalidRecommendationRequestException extends RuntimeException {

    public InvalidRecommendationRequestException() {
        super("추천 태그를 1개 이상 선택해야 합니다.");
    }
}
