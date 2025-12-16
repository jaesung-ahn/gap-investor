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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SearchPropertyServiceTest {

    @Mock
    private RealEstateDataPort realEstateDataPort;

    @InjectMocks
    private SearchPropertyService searchPropertyService;

    @Test
    @DisplayName("지역 코드로 매물을 검색할 수 있어야 한다")
    void searchProperties() {
        // given
        String regionCode = "11110";
        Property mockProperty = new Property(
                "1",
                "Mock Apt",
                new Location("Seoul", "Jongno-gu", "Sajik-dong", regionCode),
                10_000,
                8_000,
                2020,
                84.0);
        given(realEstateDataPort.fetchProperties(regionCode))
                .willReturn(List.of(mockProperty));

        // when
        List<Property> result = searchPropertyService.searchProperties(regionCode);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mock Apt");
    }
}
