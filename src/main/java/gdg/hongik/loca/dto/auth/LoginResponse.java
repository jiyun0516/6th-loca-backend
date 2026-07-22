package gdg.hongik.loca.dto.auth;

public record LoginResponse (
        String accessToken,
        String tokenType
){
}
