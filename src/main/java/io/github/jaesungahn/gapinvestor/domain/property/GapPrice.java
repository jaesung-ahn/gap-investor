package io.github.jaesungahn.gapinvestor.domain.property;

public class GapPrice {
    private final long value;

    public GapPrice(long salePrice, long jeonsePrice) {
        this.value = salePrice - jeonsePrice;
    }

    public long getValue() {
        return value;
    }
}
