package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.application.port.in.SearchPropertyUseCase;
import io.github.jaesungahn.gapinvestor.application.port.in.SortOption;
import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchPropertyService implements SearchPropertyUseCase {

    private final RealEstateDataPort realEstateDataPort;

    public SearchPropertyService(RealEstateDataPort realEstateDataPort) {
        this.realEstateDataPort = realEstateDataPort;
    }

    public List<Property> searchProperties(String regionCode, SortOption sortOption) {
        // TODO: Accept yearMonth from controller or calculate properly
        String defaultYearMonth = "202401";
        List<Property> properties = new ArrayList<>(realEstateDataPort.fetchProperties(regionCode, defaultYearMonth));

        if (sortOption != null) {
            switch (sortOption) {
                case GAP_ASC:
                    properties.sort((p1, p2) -> Long.compare(p1.getGap().getValue(), p2.getGap().getValue()));
                    break;
                case GAP_DESC:
                    properties.sort((p1, p2) -> Long.compare(p2.getGap().getValue(), p1.getGap().getValue()));
                    break;
                case JEONSE_RATE_DESC:
                    properties.sort(
                            (p1, p2) -> Double.compare(p2.getJeonseRate().getValue(), p1.getJeonseRate().getValue()));
                    break;
                case JEONSE_RATE_ASC:
                    properties.sort(
                            (p1, p2) -> Double.compare(p1.getJeonseRate().getValue(), p2.getJeonseRate().getValue()));
                    break;
            }
        }

        return properties;
    }
}
