package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.application.port.in.PropertySearchCondition;
import io.github.jaesungahn.gapinvestor.application.port.in.SearchPropertyUseCase;
import io.github.jaesungahn.gapinvestor.application.port.in.SortOption;
import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchPropertyService implements SearchPropertyUseCase {

        private final RealEstateDataPort realEstateDataPort;

        public List<Property> searchProperties(String regionCode, PropertySearchCondition condition) {
                // TODO: Accept yearMonth from controller or calculate properly
                String defaultYearMonth = "202401";
                List<Property> properties = new ArrayList<>(
                                realEstateDataPort.fetchProperties(regionCode, defaultYearMonth));

                // Filtering
                if (condition != null) {
                        properties = properties.stream()
                                        .filter(p -> condition.getMinSalePrice() == null
                                                        || p.getSalePrice() >= condition.getMinSalePrice())
                                        .filter(p -> condition.getMaxSalePrice() == null
                                                        || p.getSalePrice() <= condition.getMaxSalePrice())
                                        .filter(p -> condition.getMinJeonsePrice() == null
                                                        || p.getJeonsePrice() >= condition.getMinJeonsePrice())
                                        .filter(p -> condition.getMaxJeonsePrice() == null
                                                        || p.getJeonsePrice() <= condition.getMaxJeonsePrice())
                                        .filter(p -> condition.getMinGap() == null
                                                        || p.getGap().getValue() >= condition.getMinGap())
                                        .filter(p -> condition.getMaxGap() == null
                                                        || p.getGap().getValue() <= condition.getMaxGap())
                                        .filter(p -> condition.getMinJeonseRate() == null
                                                        || p.getJeonseRate().getValue() >= condition.getMinJeonseRate())
                                        .filter(p -> condition.getMaxJeonseRate() == null
                                                        || p.getJeonseRate().getValue() <= condition.getMaxJeonseRate())
                                        .filter(p -> condition.getMinExclusiveArea() == null
                                                        || p.getExclusiveArea() >= condition.getMinExclusiveArea())
                                        .filter(p -> condition.getMaxExclusiveArea() == null
                                                        || p.getExclusiveArea() <= condition.getMaxExclusiveArea())
                                        .filter(p -> condition.getMinBuildYear() == null
                                                        || p.getBuildYear() >= condition.getMinBuildYear())
                                        .collect(Collectors.toList());
                }

                SortOption sortOption = (condition != null) ? condition.getSort() : null;

                if (sortOption != null) {
                        switch (sortOption) {
                                case GAP_ASC:
                                        properties.sort((p1, p2) -> Long.compare(p1.getGap().getValue(),
                                                        p2.getGap().getValue()));
                                        break;
                                case GAP_DESC:
                                        properties.sort((p1, p2) -> Long.compare(p2.getGap().getValue(),
                                                        p1.getGap().getValue()));
                                        break;
                                case JEONSE_RATE_DESC:
                                        properties.sort(
                                                        (p1, p2) -> Double.compare(p2.getJeonseRate().getValue(),
                                                                        p1.getJeonseRate().getValue()));
                                        break;
                                case JEONSE_RATE_ASC:
                                        properties.sort(
                                                        (p1, p2) -> Double.compare(p1.getJeonseRate().getValue(),
                                                                        p2.getJeonseRate().getValue()));
                                        break;
                        }
                }

                return properties;
        }
}
