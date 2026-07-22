package gdg.hongik.loca.dto.auth;

import gdg.hongik.loca.entity.User;

// 회원가입 응답 DTO
public record SignupResponse(
        Integer userId,
        String email,
        String nickname
) {
    // 엔티티 -> 응답 매핑
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getUserId(), user.getEmail(), user.getNickname());
    }
}
