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
public class MolitAptTradeResponse {

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
        @JacksonXmlProperty(localName = "\uAC70\uB194\uAE08\uC561") // 거래금액
        private String dealAmount;

        @JacksonXmlProperty(localName = "\uAC74\uCD95\uB144\uB3C4") // 건축년도
        private int buildYear;

        @JacksonXmlProperty(localName = "\uB144") // 년
        private int dealYear;

        @JacksonXmlProperty(localName = "\uBC95\uC815\uB3D9") // 법정동
        private String dong;

        @JacksonXmlProperty(localName = "\uC544\uD30C\uD2B8") // 아파트
        private String apartmentName;

        @JacksonXmlProperty(localName = "\uC6D4") // 월
        private int dealMonth;

        @JacksonXmlProperty(localName = "\uC77C") // 일
        private int dealDay;

        @JacksonXmlProperty(localName = "\uC804\uC6A9\uBA74\uC801") // 전용면적
        private double exclusiveArea;

        @JacksonXmlProperty(localName = "\uC9C0\uBC88") // 지번
        private String jibun;

        @JacksonXmlProperty(localName = "\uC9C0\uC5ED\uCF54\uB4DC") // 지역코드
        private String regionCode;

        @JacksonXmlProperty(localName = "\uCE35") // 층
        private int floor;

        // Note: For actual gap investment, we need both Trade (Sales) and Rent (Jeonse)
        // data.
        // This DTO structure matches the "AptTrade" (Sales) API.
        // We will likely need a similar one for "AptRent" later or reuse/adapt this.
    }
}
