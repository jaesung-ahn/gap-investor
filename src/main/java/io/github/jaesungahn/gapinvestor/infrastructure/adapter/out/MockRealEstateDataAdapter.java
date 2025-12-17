package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockRealEstateDataAdapter implements RealEstateDataPort {

    @Override
    public List<Property> fetchProperties(String regionCode, String yearMonth) {
        List<Property> properties = new ArrayList<>();

        // Dummy Data 1
        properties.add(new Property(
                "p1",
                "Mock Raemian",
                new Location("Seoul", "Gangnam-gu", "Dogok-dong", regionCode),
                2000000000L, // Sale: 20억
                1200000000L, // Jeonse: 12억 (Gap: 8억)
                2010,
                84.9));

        // Dummy Data 2
        properties.add(new Property(
                "p2",
                "Mock Xi",
                new Location("Seoul", "Mapo-gu", "Ahyeon-dong", regionCode),
                1500000000L, // Sale: 15억
                1000000000L, // Jeonse: 10억 (Gap: 5억)
                2015,
                59.9));

        return properties;
    }
}
