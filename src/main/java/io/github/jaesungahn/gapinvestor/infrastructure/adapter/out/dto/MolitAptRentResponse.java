package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MolitAptRentResponse {

    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "body")
    private Body body;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;

        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JacksonXmlProperty(localName = "items")
        private Items items;

        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;

        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        @JacksonXmlProperty(localName = "item")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Item> itemList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JacksonXmlProperty(localName = "보증금액") // 보증금액
        private String depositAmount; // Jeonse Amount

        @JacksonXmlProperty(localName = "건축년도") // 건축년도
        private int buildYear;

        @JacksonXmlProperty(localName = "년") // 년
        private int dealYear;

        @JacksonXmlProperty(localName = "법정동") // 법정동
        private String dong;

        @JacksonXmlProperty(localName = "아파트") // 아파트
        private String apartmentName;

        @JacksonXmlProperty(localName = "월") // 월
        private int dealMonth;

        @JacksonXmlProperty(localName = "일") // 일
        private int dealDay;

        @JacksonXmlProperty(localName = "전용면적") // 전용면적
        private double exclusiveArea;

        @JacksonXmlProperty(localName = "지번") // 지번
        private String jibun;

        @JacksonXmlProperty(localName = "지역코드") // 지역코드
        private String regionCode;

        @JacksonXmlProperty(localName = "층") // 층
        private int floor;

        @JacksonXmlProperty(localName = "월세금액") // 월세금액
        private String monthlyRent; // If 0, it's Jeonse
    }
}
