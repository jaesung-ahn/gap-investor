package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.BookmarkEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.PropertyEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
// @ActiveProfiles("test") // Not needed if we use default H2 config in
// application.properties or src/test/resources
class BookmarkRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyJpaRepository propertyRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("User와 Property를 연결하여 Bookmark를 저장한다")
    void saveBookmark_shouldPersistRelationshipsAndAuditing() {
        // Given
        UserEntity user = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .nickname("Tester")
                .build();
        userRepository.save(user);

        PropertyEntity property = PropertyEntity.builder()
                .id("11110-101-001")
                .regionCode("11110")
                .dong("Sajik-dong")
                .apartmentName("Space Bon")
                .build();
        propertyRepository.save(property);

        // When
        BookmarkEntity bookmark = BookmarkEntity.builder()
                .user(user)
                .property(property)
                .snapshotSalePrice(50000L)
                .snapshotJeonsePrice(40000L)
                .build();
        BookmarkEntity savedBookmark = bookmarkRepository.save(bookmark);

        // Then
        assertThat(savedBookmark.getId()).isNotNull();
        assertThat(savedBookmark.getCreatedAt()).isNotNull(); // Auditing check

        BookmarkEntity found = bookmarkRepository.findById(savedBookmark.getId()).orElseThrow();
        assertThat(found.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(found.getProperty().getApartmentName()).isEqualTo("Space Bon");
        assertThat(found.getSnapshotSalePrice()).isEqualTo(50000L);
    }
}
