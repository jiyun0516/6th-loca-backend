package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.auth.SignupRequest;
import gdg.hongik.loca.entity.User;
import gdg.hongik.loca.exception.DuplicateEmailException;
import gdg.hongik.loca.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdg.hongik.loca.dto.auth.LoginRequest;
import gdg.hongik.loca.dto.auth.LoginResponse;
import gdg.hongik.loca.exception.InvalidCredentialsException;
import gdg.hongik.loca.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Integer signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .admin(false)
                .build();

        return userRepository.save(user).getUserId();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken =
                jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail(), user.isAdmin());

        return new LoginResponse(accessToken, "Bearer", user.isAdmin());
    }
}
