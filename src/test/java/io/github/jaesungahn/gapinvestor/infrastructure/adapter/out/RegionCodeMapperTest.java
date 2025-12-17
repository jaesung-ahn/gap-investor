package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegionCodeMapperTest {

    @Test
    @DisplayName("지정된 지역 코드로 시/도 정보를 가져와야 한다")
    void getCity_shouldReturnCorrectCity() {
        assertThat(RegionCodeMapper.getCity("11110")).isEqualTo("Seoul");
        assertThat(RegionCodeMapper.getCity("11680")).isEqualTo("Seoul");
    }

    @Test
    @DisplayName("지정된 지역 코드로 구/군 정보를 가져와야 한다")
    void getDistrict_shouldReturnCorrectDistrict() {
        assertThat(RegionCodeMapper.getDistrict("11110")).isEqualTo("Jongno-gu");
        assertThat(RegionCodeMapper.getDistrict("11680")).isEqualTo("Gangnam-gu");
    }

    @Test
    @DisplayName("알 수 없는 코드에 대해서는 Unknown을 반환해야 한다")
    void shouldReturnUnknownForInvalidCode() {
        String invalidCode = "99999";
        assertThat(RegionCodeMapper.getCity(invalidCode)).isEqualTo("Unknown");
        assertThat(RegionCodeMapper.getDistrict(invalidCode)).isEqualTo("Unknown");
    }
}
