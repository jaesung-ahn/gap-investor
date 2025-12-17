package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.dto.MolitAptTradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary // Use this as the main adapter
public class MolitRealEstateDataAdapter implements RealEstateDataPort {

    @Value("${api.public-data.gonggong.key}")
    private String serviceKey;

    @Value("${api.public-data.gonggong.url}")
    private String baseUrl;

    private final RestClient restClient;
    private final XmlMapper xmlMapper;

    public MolitRealEstateDataAdapter(RestClient.Builder builder) {
        this.restClient = builder.build();
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public List<Property> fetchProperties(String regionCode, String yearMonth) {
        log.info("Fetching real estate data for region: {} and date: {}", regionCode, yearMonth);

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("LAWD_CD", regionCode)
                .queryParam("DEAL_YMD", yearMonth)
                .queryParam("serviceKey", serviceKey)
                .build(true) // encoded=true because serviceKey is already encoded
                .toUri();

        try {
            byte[] responseBytes = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(byte[].class);

            String responseXml = new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8);

            log.debug("Response from MOLIT: {}", responseXml);

            MolitAptTradeResponse response = xmlMapper.readValue(responseXml, MolitAptTradeResponse.class);
            return mapToProperties(response);

        } catch (Exception e) {
            log.error("Failed to fetch data from MOLIT API", e);
            throw new RuntimeException("External API Call Failed", e);
        }
    }

    private List<Property> mapToProperties(MolitAptTradeResponse response) {
        if (response == null || response.getBody() == null || response.getBody().getItems() == null
                || response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }

        return response.getBody().getItems().getItemList().stream()
                .map(this::mapToProperty)
                .collect(Collectors.toList());
    }

    private Property mapToProperty(MolitAptTradeResponse.Item item) {
        // ID generation (Composite key-ish)
        String id = String.format("%s-%s-%s-%d", item.getRegionCode(), item.getDong().trim(),
                item.getApartmentName().trim(),
                item.getFloor());

        // Location
        String city = RegionCodeMapper.getCity(item.getRegionCode());
        String district = RegionCodeMapper.getDistrict(item.getRegionCode());
        Location location = new Location(city, district, item.getDong().trim(), item.getRegionCode());

        // Price Parsing (e.g. "50,000" -> 50000)
        long salePrice = parsePrice(item.getDealAmount());

        return new Property(
                id,
                item.getApartmentName().trim(),
                location,
                salePrice,
                0, // Jeonse price not available in this trade API
                item.getBuildYear(),
                item.getExclusiveArea());
    }

    private long parsePrice(String priceStr) {
        if (priceStr == null)
            return 0;
        return Long.parseLong(priceStr.replace(",", "").trim());
    }
}
