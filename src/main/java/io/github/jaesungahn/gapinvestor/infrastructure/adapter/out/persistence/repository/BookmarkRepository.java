package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.BookmarkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {
    List<BookmarkEntity> findAllByUserId(Long userId);

    Optional<BookmarkEntity> findByUserIdAndPropertyId(Long userId, String propertyId);

    boolean existsByUserIdAndPropertyId(Long userId, String propertyId);
}
