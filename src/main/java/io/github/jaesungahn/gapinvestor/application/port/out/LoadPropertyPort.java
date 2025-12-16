package io.github.jaesungahn.gapinvestor.application.port.out;

import io.github.jaesungahn.gapinvestor.domain.property.Property;
import java.util.List;

public interface LoadPropertyPort {
    List<Property> loadAllProperties();
}
