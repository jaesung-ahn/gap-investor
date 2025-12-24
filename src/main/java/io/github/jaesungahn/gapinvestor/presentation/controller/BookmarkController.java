package io.github.jaesungahn.gapinvestor.presentation.controller;

import io.github.jaesungahn.gapinvestor.application.service.BookmarkService;
import io.github.jaesungahn.gapinvestor.infrastructure.security.UserPrincipal;
import io.github.jaesungahn.gapinvestor.presentation.dto.AddBookmarkRequest;
import io.github.jaesungahn.gapinvestor.presentation.dto.BookmarkResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<Void> addBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddBookmarkRequest request) {
        bookmarkService.addBookmark(userPrincipal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String propertyId) {
        bookmarkService.removeBookmark(userPrincipal.getId(), propertyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponse>> getBookmarks(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<BookmarkResponse> bookmarks = bookmarkService.getBookmarks(userPrincipal.getId());
        return ResponseEntity.ok(bookmarks);
    }
}
