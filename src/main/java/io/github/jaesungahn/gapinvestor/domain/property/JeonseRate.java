package io.github.jaesungahn.gapinvestor.domain.property;

public class JeonseRate {
    private final double value;

    public JeonseRate(long salePrice, long jeonsePrice) {
        if (salePrice <= 0) {
            this.value = 0.0;
        } else {
            this.value = (double) jeonsePrice / salePrice * 100.0;
        }
    }

    public double getValue() {
        return value;
    }
}
