package gdg.hongik.loca.exception;

// 요청한 방문 기록을 찾을 수 없을 때 발생하는 예외
// - 미존재/소유자 불일치 공통 사용 (하드 삭제라 "삭제됨" 상태 없음)
public class VisitRecordNotFoundException extends RuntimeException {

    public VisitRecordNotFoundException(Long visitId) {
        super("방문 기록을 찾을 수 없습니다. visitId=" + visitId);
    }
}
