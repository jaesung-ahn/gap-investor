package io.github.jaesungahn.gapinvestor.domain.property;

import io.github.jaesungahn.gapinvestor.domain.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Property {
    private final String id; // Unique ID
    private final String name; // 단지명 (e.g., 자이아파트)
    private final Location location; // 위치
    private long salePrice; // 매매가
    private long jeonsePrice; // 전세가
    private int buildYear; // 준공년도
    private double exclusiveArea; // 전용면적

    public GapPrice getGap() {
        return new GapPrice(salePrice, jeonsePrice);
    }

    public JeonseRate getJeonseRate() {
        return new JeonseRate(salePrice, jeonsePrice);
    }

    public void updatePrices(long salePrice, long jeonsePrice) {
        this.salePrice = salePrice;
        this.jeonsePrice = jeonsePrice;
    }
}
