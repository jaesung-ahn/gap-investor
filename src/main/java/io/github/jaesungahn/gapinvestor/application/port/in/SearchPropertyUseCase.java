package io.github.jaesungahn.gapinvestor.application.port.in;

import io.github.jaesungahn.gapinvestor.domain.property.Property;
import java.util.List;

public interface SearchPropertyUseCase {
    List<Property> searchProperties(String regionCode);
}
