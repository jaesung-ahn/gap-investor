package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPropertyServiceTest {

    @Mock
    private RealEstateDataPort realEstateDataPort;

    @InjectMocks
    private SearchPropertyService searchPropertyService;

    @Test
    void searchProperties_shouldReturnProperties() {
        // Given
        Property property = new Property("1", "Test Apt", new Location("Seoul", "Gangnam", "Yeoksam", "11110"), 100000,
                50000, 2010, 84.0);
        when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(property));

        // When
        List<Property> result = searchPropertyService.searchProperties("11110");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Apt");
    }
}
