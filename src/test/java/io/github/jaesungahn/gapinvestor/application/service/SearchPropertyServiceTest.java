package io.github.jaesungahn.gapinvestor.application.service;

import io.github.jaesungahn.gapinvestor.application.port.in.PropertySearchCondition;
import io.github.jaesungahn.gapinvestor.application.port.in.SortOption;
import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.entity.PropertyEntity;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.persistence.repository.PropertyJpaRepository;
import io.github.jaesungahn.gapinvestor.infrastructure.mapper.PropertyMapper;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPropertyServiceTest {

        @Mock
        private RealEstateDataPort realEstateDataPort;

        @Mock
        private PropertyJpaRepository propertyJpaRepository;

        @Mock
        private PropertyMapper propertyMapper;

        @InjectMocks
        private SearchPropertyService searchPropertyService;

        @Test
        @DisplayName("기본 정렬로 매물을 검색한다")
        void searchProperties_shouldReturnProperties() {
                // Given
                Property property = new Property("1", "Test Apt", new Location("Seoul", "Gangnam", "Yeoksam", "11110"),
                                100000,
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
                Property p1 = new Property("1", "Small Gap", new Location("Seoul", "Gangnam", "Yeoksam", "11110"),
                                50000, 40000,
                                2010, 84.0); // Gap 10000
                Property p2 = new Property("2", "Large Gap", new Location("Seoul", "Gangnam", "Daechi", "11110"), 50000,
                                10000,
                                2015, 59.0); // Gap 40000

                when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(p2, p1)); // Return
                                                                                                                // unrelated
                                                                                                                // order

                // When
                PropertySearchCondition condition = PropertySearchCondition.builder()
                                .sort(SortOption.GAP_ASC)
                                .build();
                List<Property> result = searchPropertyService.searchProperties("11110", condition);

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
                Property p1 = new Property("1", "High Rate", new Location("Seoul", "Gangnam", "Yeoksam", "11110"),
                                100000,
                                80000, 2010, 84.0);
                // P2: Sale 100, Jeonse 50 -> Rate 50%
                Property p2 = new Property("2", "Low Rate", new Location("Seoul", "Gangnam", "Daechi", "11110"), 100000,
                                50000,
                                2015, 59.0);

                when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(p2, p1));

                // When
                PropertySearchCondition condition = PropertySearchCondition.builder()
                                .sort(SortOption.JEONSE_RATE_DESC)
                                .build();
                List<Property> result = searchPropertyService.searchProperties("11110", condition);

                // Then
                assertThat(result).hasSize(2);
                assertThat(result.get(0)).isEqualTo(p1); // High rate first (80%)
                assertThat(result.get(1)).isEqualTo(p2); // Low rate second (50%)
        }

        @Test
        @DisplayName("매매가 범위로 필터링한다")
        void searchProperties_shouldFilterBySalePrice() {
                // Given
                Property p1 = new Property("1", "Cheap", new Location("Seoul", "Gangnam", "Yeoksam", "11110"), 50000,
                                40000,
                                2010, 84.0);
                Property p2 = new Property("2", "Expensive", new Location("Seoul", "Gangnam", "Daechi", "11110"),
                                100000, 80000,
                                2015, 59.0);

                when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(p1, p2));

                // When
                PropertySearchCondition condition = PropertySearchCondition.builder()
                                .minSalePrice(40000L)
                                .maxSalePrice(60000L)
                                .build();
                List<Property> result = searchPropertyService.searchProperties("11110", condition);

                // Then
                assertThat(result).hasSize(1);
                assertThat(result.get(0)).isEqualTo(p1);
        }

        @Test
        @DisplayName("갭 가격 범위로 필터링한다")
        void searchProperties_shouldFilterByGapPrice() {
                // Given
                Property p1 = new Property("1", "Small Gap", new Location("Seoul", "Gangnam", "Yeoksam", "11110"),
                                50000, 45000,
                                2010, 84.0); // Gap 5000
                Property p2 = new Property("2", "Large Gap", new Location("Seoul", "Gangnam", "Daechi", "11110"),
                                100000, 50000,
                                2015, 59.0); // Gap 50000

                when(realEstateDataPort.fetchProperties(eq("11110"), anyString())).thenReturn(List.of(p1, p2));

                // When
                PropertySearchCondition condition = PropertySearchCondition.builder()
                                .maxGap(10000L)
                                .build();
                List<Property> result = searchPropertyService.searchProperties("11110", condition);

                // Then
                assertThat(result).hasSize(1);
                assertThat(result.get(0)).isEqualTo(p1);
        }

        @Test
        @DisplayName("DB에 존재하는 매물을 조회하면 반환한다")
        void getProperty_shouldReturnProperty_whenExists() {
                // Given
                String propertyId = "test-id";
                PropertyEntity entity = PropertyEntity
                                .builder()
                                .id(propertyId)
                                .apartmentName("Test Apt")
                                .regionCode("11110")
                                .dong("Sajik-dong")
                                .build();

                Property domainProperty = new Property(propertyId, "Test Apt",
                                new Location("", "", "Sajik-dong", "11110"), 0, 0, 2020, 84.0);

                when(propertyJpaRepository.findById(propertyId)).thenReturn(java.util.Optional.of(entity));
                when(propertyMapper.toDomain(entity)).thenReturn(domainProperty);

                // When
                Property result = searchPropertyService.getProperty(propertyId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(propertyId);
                assertThat(result.getName()).isEqualTo("Test Apt");
        }

        @Test
        @DisplayName("DB에 존재하지 않는 매물을 조회하면 예외를 던진다")
        void getProperty_shouldThrowException_whenNotExists() {
                // Given
                String propertyId = "unknown-id";
                when(propertyJpaRepository.findById(propertyId)).thenReturn(java.util.Optional.empty());

                // When & Then
                org.assertj.core.api.Assertions.assertThatThrownBy(() -> searchPropertyService.getProperty(propertyId))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("Property not found");
        }
}
