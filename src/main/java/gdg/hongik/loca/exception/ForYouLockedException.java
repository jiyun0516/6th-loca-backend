package gdg.hongik.loca.exception;

public class ForYouLockedException extends RuntimeException {

    public ForYouLockedException() {
        super("ForYou 추천을 이용하려면 리뷰를 3개 이상 작성해야 합니다.");
    }
}