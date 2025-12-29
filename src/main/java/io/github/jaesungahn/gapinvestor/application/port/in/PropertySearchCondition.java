package io.github.jaesungahn.gapinvestor.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class PropertySearchCondition {
    private Long minSalePrice;
    private Long maxSalePrice;
    private Long minJeonsePrice;
    private Long maxJeonsePrice;
    private Long minGap;
    private Long maxGap;
    private Double minJeonseRate;
    private Double maxJeonseRate;
    private Double minExclusiveArea;
    private Double maxExclusiveArea;
    private Integer minBuildYear;
    private String yearMonth;

    private SortOption sort;
}
