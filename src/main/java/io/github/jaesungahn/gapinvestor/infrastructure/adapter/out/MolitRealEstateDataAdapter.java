package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.dto.MolitAptRentResponse;
import io.github.jaesungahn.gapinvestor.infrastructure.adapter.out.dto.MolitAptTradeResponse;
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

    @Value("${api.public-data.gonggong.rent-url}")
    private String rentUrl;

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

        // 1. Fetch Trade Data
        MolitAptTradeResponse tradeResponse = fetchTradeData(regionCode, yearMonth);
        List<Property> properties = mapToProperties(tradeResponse);

        if (properties.isEmpty()) {
            return properties;
        }

        // 2. Fetch Rent Data
        MolitAptRentResponse rentResponse = fetchRentData(regionCode, yearMonth);
        List<MolitAptRentResponse.Item> rentItems = getRentItems(rentResponse);

        // 3. Merge Jeonse Prices
        for (Property property : properties) {
            long maxJeonsePrice = findMaxJeonsePrice(property, rentItems);
            if (maxJeonsePrice > 0) {
                property.updatePrices(property.getSalePrice(), maxJeonsePrice);
            }
        }

        return properties;
    }

    private MolitAptTradeResponse fetchTradeData(String regionCode, String yearMonth) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("LAWD_CD", regionCode)
                .queryParam("DEAL_YMD", yearMonth)
                .queryParam("serviceKey", serviceKey)
                .build(true)
                .toUri();

        return fetchFromApi(uri, MolitAptTradeResponse.class);
    }

    private MolitAptRentResponse fetchRentData(String regionCode, String yearMonth) {
        URI uri = UriComponentsBuilder.fromHttpUrl(rentUrl)
                .queryParam("LAWD_CD", regionCode)
                .queryParam("DEAL_YMD", yearMonth)
                .queryParam("serviceKey", serviceKey)
                .build(true)
                .toUri();

        return fetchFromApi(uri, MolitAptRentResponse.class);
    }

    private <T> T fetchFromApi(URI uri, Class<T> responseType) {
        try {
            byte[] responseBytes = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(byte[].class);
            String responseXml = new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8);
            log.debug("Response from API: {}", responseXml);
            return xmlMapper.readValue(responseXml, responseType);
        } catch (Exception e) {
            log.error("Failed to fetch data from API: {}", uri, e);
            // Return null or empty object to continue execution?
            // Throwing exception might block the whole process if one API fails.
            // Let's rethrow for now as per original logic.
            throw new RuntimeException("External API Call Failed", e);
        }
    }

    private List<MolitAptRentResponse.Item> getRentItems(MolitAptRentResponse response) {
        if (response == null || response.getBody() == null || response.getBody().getItems() == null
                || response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }
        return response.getBody().getItems().getItemList();
    }

    private long findMaxJeonsePrice(Property property, List<MolitAptRentResponse.Item> rentItems) {
        return rentItems.stream()
                .filter(item -> isSameApartment(property, item))
                .mapToLong(item -> parsePrice(item.getDepositAmount()))
                .max()
                .orElse(0);
    }

    private boolean isSameApartment(Property property, MolitAptRentResponse.Item item) {
        // 1. Check Dong
        if (!property.getLocation().getDong().equals(item.getDong().trim())) {
            return false;
        }
        // 2. Check Apartment Name (Exact match after trim)
        if (!property.getName().equals(item.getApartmentName().trim())) {
            return false;
        }
        // 3. Check Exclusive Area (Approximate match +/- 3.0 m2 to encompass slight
        // variations)
        double diff = Math.abs(property.getExclusiveArea() - item.getExclusiveArea());
        return diff <= 3.0; // Reasonable margin
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
                0, // Jeonse price initially 0, will be updated
                item.getBuildYear(),
                item.getExclusiveArea());
    }

    private long parsePrice(String priceStr) {
        if (priceStr == null)
            return 0;
        return Long.parseLong(priceStr.replace(",", "").trim());
    }
}
