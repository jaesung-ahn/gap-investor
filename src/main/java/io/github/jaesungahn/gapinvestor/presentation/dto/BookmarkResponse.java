package io.github.jaesungahn.gapinvestor.presentation.dto;

import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.BookmarkEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.PropertyEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkResponse {
    private Long id; // Bookmark ID
    private Property property; // Domain model
    private Long snapshotSalePrice;
    private Long snapshotJeonsePrice;
    private LocalDateTime createdAt;

    public static BookmarkResponse from(BookmarkEntity entity) {
        PropertyEntity pe = entity.getProperty();
        Property propertyDomain = new Property(
                pe.getId(),
                pe.getApartmentName(),
                new Location("", "", pe.getDong(), pe.getRegionCode()),
                entity.getSnapshotSalePrice(), // show snapshot price? or real price? Domain usually has real price.
                // Let's use snapshot price for the display in bookmark list as "price when
                // saved"
                // OR we might want to fetch current price. For now, let's map entity values.
                entity.getSnapshotJeonsePrice(),
                pe.getBuildYear(),
                pe.getExclusiveArea());

        return BookmarkResponse.builder()
                .id(entity.getId())
                .property(propertyDomain)
                .snapshotSalePrice(entity.getSnapshotSalePrice())
                .snapshotJeonsePrice(entity.getSnapshotJeonsePrice())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
