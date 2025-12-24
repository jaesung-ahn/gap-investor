package io.github.jaesungahn.gapinvestor.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaesungahn.gapinvestor.application.service.AuthService;
import io.github.jaesungahn.gapinvestor.application.service.AuthService.LoginCommand;
import io.github.jaesungahn.gapinvestor.application.service.AuthService.SignUpCommand;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 시 200 OK를 반환한다")
    void signUp_success() throws Exception {
        SignUpCommand command = SignUpCommand.builder()
                .email("newuser@example.com")
                .password("password123")
                .nickname("Newbie")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰을 반환한다")
    void login_success() throws Exception {
        // Given: Create a user directly
        userRepository.save(
                io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity.builder()
                        .email("login@example.com")
                        .password(passwordEncoder.encode("password123"))
                        .nickname("LoginUser")
                        .build());

        // When & Then
        LoginCommand command = LoginCommand.builder()
                .email("login@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}
