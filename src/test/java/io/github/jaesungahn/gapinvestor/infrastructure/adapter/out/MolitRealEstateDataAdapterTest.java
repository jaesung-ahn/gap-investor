package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import io.github.jaesungahn.gapinvestor.domain.property.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

import org.springframework.test.util.ReflectionTestUtils;

class MolitRealEstateDataAdapterTest {

  private MolitRealEstateDataAdapter adapter;
  private MockRestServiceServer mockServer;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();
    mockServer = MockRestServiceServer.bindTo(builder).build();
    adapter = new MolitRealEstateDataAdapter(builder);

    ReflectionTestUtils.setField(adapter, "serviceKey", "test-key");
    ReflectionTestUtils.setField(adapter, "baseUrl",
        "http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev");
  }

  private String k(int... codes) {
    StringBuilder sb = new StringBuilder();
    for (int c : codes)
      sb.append((char) c);
    return sb.toString();
  }

  @Test
  void fetchProperties_shouldParseXmlResponseCorrectly() {
    // Given
    // Construct strings at runtime to ensure safety against source encoding issues
    String dealAmount = k(0xAC70, 0xB194, 0xAE08, 0xC561); // 거래금액
    String buildYear = k(0xAC74, 0xCD95, 0xB144, 0xB3C4); // 건축년도
    String year = k(0xB144); // 년
    String dong = k(0xBC95, 0xC815, 0xB3D9); // 법정동
    String apartment = k(0xC544, 0xD30C, 0xD2B8); // 아파트
    String month = k(0xC6D4); // 월
    String day = k(0xC77C); // 일
    String area = k(0xC804, 0xC6A9, 0xBA74, 0xC801); // 전용면적
    String jibun = k(0xC9C0, 0xBC88); // 지번
    String regionCode = k(0xC9C0, 0xC5ED, 0xCF54, 0xB4DC); // 지역코드
    String floor = k(0xCE35); // 층

    String sampleXml = "<response>" +
        "<header><resultCode>00</resultCode><resultMsg>NORMAL SERVICE.</resultMsg></header>" +
        "<body><items><item>" +
        "<" + dealAmount + ">    82,500</" + dealAmount + ">" +
        "<" + buildYear + ">2008</" + buildYear + ">" +
        "<" + year + ">2015</" + year + ">" +
        "<" + dong + "> 사직동</" + dong + ">" +
        "<" + apartment + ">광화문풍림스페이스본</" + apartment + ">" +
        "<" + month + ">12</" + month + ">" +
        "<" + day + ">10</" + day + ">" +
        "<" + area + ">94.51</" + area + ">" +
        "<" + jibun + ">9</" + jibun + ">" +
        "<" + regionCode + ">11110</" + regionCode + ">" +
        "<" + floor + ">11</" + floor + ">" +
        "</item></items>" +
        "<numOfRows>10</numOfRows><pageNo>1</pageNo><totalCount>34</totalCount>" +
        "</body></response>";

    mockServer.expect(requestTo(containsString("http://openapi.molit.go.kr")))
        .andRespond(withSuccess(sampleXml.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            new MediaType("application", "xml", java.nio.charset.StandardCharsets.UTF_8)));

    // When
    List<Property> properties = adapter.fetchProperties("11110");

    // Then
    assertThat(properties).hasSize(1);
    Property property = properties.get(0);
    assertThat(property.getName()).isEqualTo("광화문풍림스페이스본");
    assertThat(property.getSalePrice()).isEqualTo(82500);
    assertThat(property.getBuildYear()).isEqualTo(2008);
    assertThat(property.getLocation().getDong()).isEqualTo("사직동");
  }
}
