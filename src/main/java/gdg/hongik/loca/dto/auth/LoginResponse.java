package gdg.hongik.loca.dto.auth;

// 로그인 응답 DTO
// - tokenType 고정 "Bearer"
public record LoginResponse(
        String accessToken,
        String tokenType
) {
    public static LoginResponse of(String token) {
        return new LoginResponse(token, "Bearer");
    }
}
