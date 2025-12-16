package io.github.jaesungahn.gapinvestor.infrastructure.adapter.out;

import io.github.jaesungahn.gapinvestor.application.port.out.RealEstateDataPort;
import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
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

@Slf4j
@Component
@Primary // Use this as the main adapter
public class MolitRealEstateDataAdapter implements RealEstateDataPort {

    @Value("${api.public-data.gonggong.key}")
    private String serviceKey;

    @Value("${api.public-data.gonggong.url}")
    private String baseUrl;

    private final RestClient restClient;

    public MolitRealEstateDataAdapter(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public List<Property> fetchProperties(String regionCode) {
        log.info("Fetching real estate data for region: {}", regionCode);

        // Note: Actual API requires date (LAWD_CD + DEAL_YMD)
        // For MVP, we default to a specific month or handle logic
        String dealYmd = "202401";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("LAWD_CD", regionCode)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("serviceKey", serviceKey)
                .build(true) // encoded=true because serviceKey is already encoded
                .toUri();

        try {
            // Raw response for now as parsing XML requires binding
            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            log.info("Response from MOLIT: {}", response);

            // TODO: Parse XML response and map to Property list
            // For now, returning empty list to verify connectivity structure
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Failed to fetch data from MOLIT API", e);
            throw new RuntimeException("External API Call Failed", e);
        }
    }
}
