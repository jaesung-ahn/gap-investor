package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.BookmarkEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.PropertyEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.BookmarkRepository;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.PropertyJpaRepository;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.UserRepository;
import io.github.jaesungahn.gapinvestor.presentation.dto.AddBookmarkRequest;
import io.github.jaesungahn.gapinvestor.presentation.dto.BookmarkResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PropertyJpaRepository propertyRepository;
    private final UserRepository userRepository;

    public void addBookmark(Long userId, AddBookmarkRequest request) {
        // 1. Check if user exists
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 2. Check if property exists in cache (local DB)
        PropertyEntity property = propertyRepository.findById(request.getPropertyId())
                .orElseGet(() -> saveNewProperty(request));

        // 3. Check if already bookmarked
        if (bookmarkRepository.existsByUserIdAndPropertyId(userId, property.getId())) {
            // Already bookmarked, maybe just return or throw exception.
            // Idempotent is better, so just return.
            return;
        }

        // 4. Save Bookmark with snapshot prices
        BookmarkEntity bookmark = BookmarkEntity.builder()
                .user(user)
                .property(property)
                .snapshotSalePrice(request.getSalePrice())
                .snapshotJeonsePrice(request.getJeonsePrice())
                .build();

        bookmarkRepository.save(bookmark);
    }

    private PropertyEntity saveNewProperty(AddBookmarkRequest request) {
        PropertyEntity newProperty = PropertyEntity.builder()
                .id(request.getPropertyId())
                .regionCode(request.getRegionCode())
                .dong(request.getDong())
                .apartmentName(request.getApartmentName())
                .jibun(request.getJibun())
                .buildYear(request.getBuildYear())
                .exclusiveArea(request.getExclusiveArea())
                .build();

        return propertyRepository.save(newProperty);
    }

    public void removeBookmark(Long userId, String propertyId) {
        BookmarkEntity bookmark = bookmarkRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Bookmark not found"));

        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse> getBookmarks(Long userId) {
        return bookmarkRepository.findAllByUserId(userId).stream()
                .map(BookmarkResponse::from)
                .collect(Collectors.toList());
    }
}
