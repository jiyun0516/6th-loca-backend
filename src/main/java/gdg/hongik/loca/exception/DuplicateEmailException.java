package gdg.hongik.loca.exception;

// 이메일 중복(활성 사용자 기준) - 409
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("이미 사용 중인 이메일입니다. email=" + email);
    }
}
