package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.application.service.AuthService.LoginCommand;
import io.github.jaesungahn.gapinvestor.application.service.AuthService.SignUpCommand;
import io.github.jaesungahn.gapinvestor.application.service.AuthService.TokenResponse;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.UserRepository;
import io.github.jaesungahn.gapinvestor.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        // given
        SignUpCommand command = SignUpCommand.builder()
                .email("test@example.com")
                .password("password")
                .nickname("nickname")
                .build();

        given(userRepository.existsByEmail(command.getEmail())).willReturn(false);
        given(passwordEncoder.encode(command.getPassword())).willReturn("encodedPassword");

        // when
        authService.signUp(command);

        // then
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void signUp_fail_duplicateEmail() {
        // given
        SignUpCommand command = SignUpCommand.builder()
                .email("test@example.com")
                .password("password")
                .nickname("nickname")
                .build();

        given(userRepository.existsByEmail(command.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User already exists");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginCommand command = LoginCommand.builder()
                .email("test@example.com")
                .password("password")
                .build();

        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);

        Authentication authentication = mock(Authentication.class);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);

        given(jwtTokenProvider.generateToken(authentication)).willReturn("accessToken");

        // when
        TokenResponse response = authService.login(command);

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }
}
