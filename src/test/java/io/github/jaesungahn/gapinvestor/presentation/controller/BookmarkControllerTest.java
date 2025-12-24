package io.github.jaesungahn.gapinvestor.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaesungahn.gapinvestor.application.service.BookmarkService;
import io.github.jaesungahn.gapinvestor.infrastructure.security.UserPrincipal;
import io.github.jaesungahn.gapinvestor.presentation.dto.AddBookmarkRequest;
import io.github.jaesungahn.gapinvestor.presentation.dto.BookmarkResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("북마크 추가 API 호출 시 서비스가 호출된다")
    void addBookmark_ShouldCallService() throws Exception {
        // Given
        Long userId = 1L;
        UserPrincipal userPrincipal = new UserPrincipal(userId, "test@example.com", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        AddBookmarkRequest request = AddBookmarkRequest.builder()
                .propertyId("11110-101-001")
                .regionCode("11110")
                .dong("Sajik-dong")
                .apartmentName("Space Bon")
                .buildYear(2008)
                .exclusiveArea(84.5)
                .salePrice(50000L)
                .jeonsePrice(40000L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/bookmarks")
                .with(csrf())
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookmarkService).addBookmark(eq(userId), any(AddBookmarkRequest.class));
    }

    @Test
    @DisplayName("북마크 목록 조회 API 호출 시 결과를 반환한다")
    void getBookmarks_ShouldReturnList() throws Exception {
        // Given
        Long userId = 1L;
        UserPrincipal userPrincipal = new UserPrincipal(userId, "test@example.com", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        List<BookmarkResponse> responses = Collections.emptyList();
        given(bookmarkService.getBookmarks(userId)).willReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/bookmarks")
                .with(user(userPrincipal)))
                .andExpect(status().isOk());

        verify(bookmarkService).getBookmarks(userId);
    }

    @Test
    @DisplayName("북마크 삭제 API 호출 시 서비스가 호출된다")
    void removeBookmark_ShouldCallService() throws Exception {
        // Given
        Long userId = 1L;
        String propertyId = "11110-101-001";
        UserPrincipal userPrincipal = new UserPrincipal(userId, "test@example.com", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // When & Then
        mockMvc.perform(delete("/api/bookmarks/{propertyId}", propertyId)
                .with(csrf())
                .with(user(userPrincipal)))
                .andExpect(status().isOk());

        verify(bookmarkService).removeBookmark(userId, propertyId);
    }
}
