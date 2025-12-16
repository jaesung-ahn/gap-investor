package io.github.jaesungahn.gapinvestor.domain.property;

import io.github.jaesungahn.gapinvestor.domain.location.Location;

public class Property {
    private final String id; // Unique ID
    private final String name; // 단지명 (e.g., 자이아파트)
    private final Location location; // 위치
    private long salePrice; // 매매가
    private long jeonsePrice; // 전세가
    private int buildYear; // 준공년도
    private double exclusiveArea; // 전용면적

    public Property(String id, String name, Location location, long salePrice, long jeonsePrice, int buildYear,
            double exclusiveArea) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.salePrice = salePrice;
        this.jeonsePrice = jeonsePrice;
        this.buildYear = buildYear;
        this.exclusiveArea = exclusiveArea;
    }

    public GapPrice getGap() {
        return new GapPrice(salePrice, jeonsePrice);
    }

    public JeonseRate getJeonseRate() {
        return new JeonseRate(salePrice, jeonsePrice);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public long getSalePrice() {
        return salePrice;
    }

    public long getJeonsePrice() {
        return jeonsePrice;
    }

    public int getBuildYear() {
        return buildYear;
    }

    public double getExclusiveArea() {
        return exclusiveArea;
    }

    public void updatePrices(long salePrice, long jeonsePrice) {
        this.salePrice = salePrice;
        this.jeonsePrice = jeonsePrice;
    }
}
