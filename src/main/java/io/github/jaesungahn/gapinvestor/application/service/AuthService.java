package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.UserRepository;
import io.github.jaesungahn.gapinvestor.infrastructure.security.JwtTokenProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public void signUp(SignUpCommand command) {
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("User already exists with email: " + command.getEmail());
        }

        UserEntity user = UserEntity.builder()
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .nickname(command.getNickname())
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginCommand command) {
        // 1. Based on Login ID/PW, create Authentication Token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                command.getEmail(), command.getPassword());

        // 2. Actually Validating the user (calls UserDetailsService)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. Generate JWT Token
        String accessToken = jwtTokenProvider.generateToken(authentication);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }

    // DTOs (Inner classes for simplicity or move to separate files if preferred.
    // Keeping concise here.)
    @Getter
    @Builder
    public static class SignUpCommand {
        private String email;
        private String password;
        private String nickname;
    }

    @Getter
    @Builder
    public static class LoginCommand {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class TokenResponse {
        private String accessToken;
        private String tokenType;
    }
}
