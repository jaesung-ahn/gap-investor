package io.github.jaesungahn.gapinvestor.domain.property;

import io.github.jaesungahn.gapinvestor.domain.location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyTest {

    @Test
    @DisplayName("갭 가격 계산이 정확해야 한다")
    void calculateGapPrice() {
        // given
        Property property = new Property(
                "1",
                "Test Apt",
                new Location("Seoul", "Gangnam", "Dogok", "11110"),
                10_000, // 매매가
                8_000, // 전세가
                2020,
                84.0);

        // when
        GapPrice gap = property.getGap();

        // then
        assertThat(gap.getValue()).isEqualTo(2_000);
    }

    @Test
    @DisplayName("전세가율 계산이 정확해야 한다")
    void calculateJeonseRate() {
        // given
        Property property = new Property(
                "1",
                "Test Apt",
                new Location("Seoul", "Gangnam", "Dogok", "11110"),
                10_000,
                8_000,
                2020,
                84.0);

        // when
        JeonseRate rate = property.getJeonseRate();

        // then
        assertThat(rate.getValue()).isEqualTo(80.0);
    }
}
