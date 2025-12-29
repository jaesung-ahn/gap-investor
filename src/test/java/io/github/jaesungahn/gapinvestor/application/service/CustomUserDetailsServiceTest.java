package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 이름(이메일)으로 사용자 로드 성공")
    void loadUserByUsername_success() {
        // given
        String email = "test@example.com";
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .password("password")
                .nickname("nickname")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(userEntity));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // then
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("사용자 로드 실패 - 존재하지 않는 사용자")
    void loadUserByUsername_fail_notFound() {
        // given
        String email = "unknown@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
