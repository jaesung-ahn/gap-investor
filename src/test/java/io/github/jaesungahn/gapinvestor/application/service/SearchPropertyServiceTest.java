package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.application.port.in.SortOption;
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
    @DisplayName("기본 정렬로 매물을 검색한다")
    void searchProperties_shouldReturnProperties() {
        // Given
        Property property = new Property("1", "Test Apt", new Location("Seoul", "Gangnam", "Yeoksam", "11110"), 100000,
                50000, 2010, 84.0);
        when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(property));

        // When
        List<Property> result = searchPropertyService.searchProperties("11110", null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Apt");
    }

    @Test
    @DisplayName("Gap 오름차순 정렬을 확인한다")
    void searchProperties_shouldSortByGapAsc() {
        // Given
        Property p1 = new Property("1", "Small Gap", new Location("Seoul", "Gangnam", "Yeoksam", "11110"), 50000, 40000,
                2010, 84.0); // Gap 10000
        Property p2 = new Property("2", "Large Gap", new Location("Seoul", "Gangnam", "Daechi", "11110"), 50000, 10000,
                2015, 59.0); // Gap 40000

        when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(p2, p1)); // Return
                                                                                                        // unrelated
                                                                                                        // order

        // When
        List<Property> result = searchPropertyService.searchProperties("11110", SortOption.GAP_ASC);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(p1); // Small gap first
        assertThat(result.get(1)).isEqualTo(p2);
    }

    @Test
    @DisplayName("전세가율 내림차순 정렬을 확인한다")
    void searchProperties_shouldSortByJeonseRateDesc() {
        // Given
        // P1: Sale 100, Jeonse 80 -> Rate 80%
        Property p1 = new Property("1", "High Rate", new Location("Seoul", "Gangnam", "Yeoksam", "11110"), 100000,
                80000, 2010, 84.0);
        // P2: Sale 100, Jeonse 50 -> Rate 50%
        Property p2 = new Property("2", "Low Rate", new Location("Seoul", "Gangnam", "Daechi", "11110"), 100000, 50000,
                2015, 59.0);

        when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(p2, p1));

        // When
        List<Property> result = searchPropertyService.searchProperties("11110", SortOption.JEONSE_RATE_DESC);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(p1); // High rate first (80%)
        assertThat(result.get(1)).isEqualTo(p2); // Low rate second (50%)
    }
}
