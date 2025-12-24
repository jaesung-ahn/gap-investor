package io.github.jaesungahn.gapinvestor.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.BookmarkEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.PropertyEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.BookmarkRepository;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.PropertyJpaRepository;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.UserRepository;
import io.github.jaesungahn.gapinvestor.presentation.dto.AddBookmarkRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private PropertyJpaRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    @DisplayName("존재하지 않는 사용자로 북마크 추가 시 예외가 발생한다")
    void addBookmark_ThrowException_WhenUserNotFound() {
        // Given
        Long userId = 1L;
        AddBookmarkRequest request = AddBookmarkRequest.builder()
                .propertyId("prop-1")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookmarkService.addBookmark(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("이미 북마크된 매물은 중복 저장하지 않고 정상 종료한다 (Idempotent)")
    void addBookmark_ShouldReturn_WhenAlreadyExists() {
        // Given
        Long userId = 1L;
        String propertyId = "prop-1";
        AddBookmarkRequest request = AddBookmarkRequest.builder()
                .propertyId(propertyId)
                .build();

        UserEntity user = UserEntity.builder().id(userId).build();
        PropertyEntity property = PropertyEntity.builder().id(propertyId).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(propertyRepository.findById(propertyId)).willReturn(Optional.of(property));
        given(bookmarkRepository.existsByUserIdAndPropertyId(userId, propertyId)).willReturn(true);

        // When
        bookmarkService.addBookmark(userId, request);

        // Then
        verify(bookmarkRepository, never()).save(any(BookmarkEntity.class));
    }

    @Test
    @DisplayName("새로운 매물을 북마크하면 Property와 Bookmark를 저장한다")
    void addBookmark_ShouldSavePropertyAndBookmark_WhenNew() {
        // Given
        Long userId = 1L;
        String propertyId = "prop-1";
        AddBookmarkRequest request = AddBookmarkRequest.builder()
                .propertyId(propertyId)
                .salePrice(50000L)
                .jeonsePrice(40000L)
                .regionCode("11110")
                .dong("Sajik-dong")
                .apartmentName("Space Bon")
                .jibun("9")
                .buildYear(2008)
                .exclusiveArea(84.5)
                .build();

        UserEntity user = UserEntity.builder().id(userId).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        // Property not found in DB
        given(propertyRepository.findById(propertyId)).willReturn(Optional.empty());
        // Return mocked saved property
        given(propertyRepository.save(any(PropertyEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        given(bookmarkRepository.existsByUserIdAndPropertyId(userId, propertyId)).willReturn(false);

        // When
        bookmarkService.addBookmark(userId, request);

        // Then
        verify(propertyRepository).save(any(PropertyEntity.class));
        verify(bookmarkRepository).save(any(BookmarkEntity.class));
    }

    @Test
    @DisplayName("북마크 삭제 시 존재하지 않으면 예외가 발생한다")
    void removeBookmark_ThrowException_WhenNotFound() {
        // Given
        Long userId = 1L;
        String propertyId = "prop-1";

        given(bookmarkRepository.findByUserIdAndPropertyId(userId, propertyId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookmarkService.removeBookmark(userId, propertyId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bookmark not found");
    }

    @Test
    @DisplayName("북마크가 존재하면 삭제한다")
    void removeBookmark_ShouldDelete_WhenFound() {
        // Given
        Long userId = 1L;
        String propertyId = "prop-1";
        BookmarkEntity bookmark = BookmarkEntity.builder().build();

        given(bookmarkRepository.findByUserIdAndPropertyId(userId, propertyId)).willReturn(Optional.of(bookmark));

        // When
        bookmarkService.removeBookmark(userId, propertyId);

        // Then
        verify(bookmarkRepository).delete(bookmark);
    }
}
