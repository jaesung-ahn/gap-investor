package io.github.jaesungahn.gapinvestor.infrastructure.mapper;

import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.PropertyEntity;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

    public Property toDomain(PropertyEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Property(
                entity.getId(),
                entity.getApartmentName(),
                // PropertyEntity currently stores regionCode and dong separately,
                // but Location expects city, district, dong, code.
                // For now, we map available fields and use blank/default for missing ones,
                // or pass regionCode as the code.
                new Location("", "", entity.getDong(), entity.getRegionCode()),
                // PropertyEntity currently doesn't store price in the entity itself (Only
                // Bookmarks do snapshots).
                // This is a limitation. If we are fetching from DB, we might not have the
                // "current" price.
                // However, Property entity definition in domain has salePrice/jeonsePrice.
                // Local DB PropertyEntity (Cache) might need to be updated with prices if we
                // want to serve them.
                // For this MVP, if PropertyEntity doesn't store price, we return 0 or Need to
                // Refactor Entity.
                // checking PropertyEntity definition again...
                0L, // salePrice - Not in PropertyEntity
                0L, // jeonsePrice - Not in PropertyEntity
                entity.getBuildYear(),
                entity.getExclusiveArea());
    }
}
