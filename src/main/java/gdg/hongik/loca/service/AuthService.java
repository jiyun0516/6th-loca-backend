package gdg.hongik.loca.service;

import gdg.hongik.loca.config.JwtUtil;
import gdg.hongik.loca.dto.auth.LoginRequest;
import gdg.hongik.loca.dto.auth.LoginResponse;
import gdg.hongik.loca.dto.auth.SignupRequest;
import gdg.hongik.loca.dto.auth.SignupResponse;
import gdg.hongik.loca.entity.User;
import gdg.hongik.loca.exception.DuplicateEmailException;
import gdg.hongik.loca.exception.InvalidCredentialsException;
import gdg.hongik.loca.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

// 인증 서비스(회원가입/로그인)
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    // - 활성 사용자 기준 이메일 중복 체크
    // - BCrypt 해시 저장
    @Transactional
    public SignupResponse signup(@Valid SignupRequest request) {
        userRepository.findByEmailAndDeletedAtIsNull(request.email())
                .ifPresent(u -> { throw new DuplicateEmailException(request.email()); });

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        return SignupResponse.from(userRepository.save(user));
    }

    // 로그인
    // - 활성 사용자 없음/비밀번호 불일치 -> 401
    // - 성공 시 JWT 발급
    public LoginResponse login(@Valid LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.createToken(user.getUserId());
        return LoginResponse.of(token);
    }
}
