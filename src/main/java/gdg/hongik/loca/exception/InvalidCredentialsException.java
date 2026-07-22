package gdg.hongik.loca.exception;

// 로그인 실패(사용자 없음/비밀번호 불일치) - 401
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
