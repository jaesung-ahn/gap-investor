package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import io.github.jaesungahn.gapinvestor.domain.property.Property;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;

@ExtendWith(MockitoExtension.class)
class MolitRealEstateDataAdapterTest {

  @Mock
  private RestClient.Builder restClientBuilder;

  @Mock
  private RestClient restClient;

  @Mock
  private RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private RequestHeadersSpec requestHeadersSpec;

  @Mock
  private ResponseSpec responseSpec;

  private MolitRealEstateDataAdapter adapter;

  @BeforeEach
  void setUp() {
    given(restClientBuilder.build()).willReturn(restClient);
    adapter = new MolitRealEstateDataAdapter(restClientBuilder);

    // Inject properties
    ReflectionTestUtils.setField(adapter, "serviceKey", "test-key");
    ReflectionTestUtils.setField(adapter, "baseUrl", "http://test-trade.com");
    ReflectionTestUtils.setField(adapter, "rentUrl", "http://test-rent.com");
  }

  @Test
  @DisplayName("매매와 전세 데이터를 가져와서 Gap 가격을 계산한다")
  void fetchProperties_shouldMergeTradeAndRentData() {
    // Given
    String tradeXml = "<response><body><items><item>" +
        "<거래금액>80,000</거래금액><건축년도>2020</건축년도><년>2023</년><법정동>Sajik-dong</법정동>" +
        "<아파트>Test Apt</아파트><월>12</월><일>1</일><전용면적>84.0</전용면적><지역코드>11110</지역코드><층>5</층>" +
        "</item></items></body></response>";

    String rentXml = "<response><body><items><item>" +
        "<보증금액>60,000</보증금액><건축년도>2020</건축년도><년>2023</년><법정동>Sajik-dong</법정동>" +
        "<아파트>Test Apt</아파트><월>12</월><일>5</일><전용면적>84.0</전용면적><지역코드>11110</지역코드><층>3</층>" +
        "</item></items></body></response>";

    // Mock RestClient chain
    given(restClient.get()).willReturn(requestHeadersUriSpec);
    given(requestHeadersUriSpec.uri(any(URI.class))).willReturn(requestHeadersSpec);
    given(requestHeadersSpec.retrieve()).willReturn(responseSpec);

    // Return trade XML first, then rent XML
    given(responseSpec.body(byte[].class))
        .willReturn(tradeXml.getBytes(StandardCharsets.UTF_8))
        .willReturn(rentXml.getBytes(StandardCharsets.UTF_8));

    // When
    List<Property> properties = adapter.fetchProperties("11110", "202312");

    // Then
    assertThat(properties).hasSize(1);
    Property property = properties.get(0);

    assertThat(property.getName()).isEqualTo("Test Apt");
    assertThat(property.getSalePrice()).isEqualTo(80000);
    assertThat(property.getJeonsePrice()).isEqualTo(60000);
    assertThat(property.getGap().getValue()).isEqualTo(20000); // 80000 - 60000
  }

  @Test
  @DisplayName("전세 데이터가 없으면 전세가는 0이다")
  void fetchProperties_shouldHaveZeroJeonse_whenNoRentData() {
    // Given
    String tradeXml = "<response><body><items><item>" +
        "<거래금액>80,000</거래금액><건축년도>2020</건축년도><년>2023</년><법정동>Sajik-dong</법정동>" +
        "<아파트>Test Apt</아파트><월>12</월><일>1</일><전용면적>84.0</전용면적><지역코드>11110</지역코드><층>5</층>" +
        "</item></items></body></response>";

    String rentXml = "<response><body><items></items></body></response>"; // Empty rent

    // Mock RestClient chain
    given(restClient.get()).willReturn(requestHeadersUriSpec);
    given(requestHeadersUriSpec.uri(any(URI.class))).willReturn(requestHeadersSpec);
    given(requestHeadersSpec.retrieve()).willReturn(responseSpec);

    given(responseSpec.body(byte[].class))
        .willReturn(tradeXml.getBytes(StandardCharsets.UTF_8))
        .willReturn(rentXml.getBytes(StandardCharsets.UTF_8));

    // When
    List<Property> properties = adapter.fetchProperties("11110", "202312");

    // Then
    assertThat(properties).hasSize(1);
    assertThat(properties.get(0).getJeonsePrice()).isEqualTo(0);
    assertThat(properties.get(0).getGap().getValue()).isEqualTo(80000); // 80000 - 0
  }
}
