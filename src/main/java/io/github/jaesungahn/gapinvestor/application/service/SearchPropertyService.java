package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.application.port.in.SearchPropertyUseCase;
import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchPropertyService implements SearchPropertyUseCase {

    private final RealEstateDataPort realEstateDataPort;

    public SearchPropertyService(RealEstateDataPort realEstateDataPort) {
        this.realEstateDataPort = realEstateDataPort;
    }

    @Override
    public List<Property> searchProperties(String regionCode) {
        return realEstateDataPort.fetchProperties(regionCode);
    }
}
