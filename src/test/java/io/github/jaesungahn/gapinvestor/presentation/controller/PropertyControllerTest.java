package io.github.jaesungahn.gapinvestor.presentation.controller;

import io.github.jaesungahn.gapinvestor.application.port.in.SearchPropertyUseCase;
import io.github.jaesungahn.gapinvestor.domain.location.Location;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.context.annotation.Import;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropertyController.class)
@Import(io.github.jaesungahn.gapinvestor.infrastructure.config.SecurityConfig.class)
class PropertyControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private SearchPropertyUseCase searchPropertyUseCase;

        @MockBean
        private io.github.jaesungahn.gapinvestor.infrastructure.security.JwtTokenProvider jwtTokenProvider;

        @Test
        @DisplayName("매물 검색 API 호출 성공")
        void searchProperties() throws Exception {
                // given
                String regionCode = "11110";
                Property mockProperty = new Property(
                                "1",
                                "Mock Apt",
                                new Location("Seoul", "Jongno-gu", "Sajik-dong", regionCode),
                                10_000,
                                8_000,
                                2020,
                                84.0);

                given(searchPropertyUseCase.searchProperties(eq("11110"), any()))
                                .willReturn(List.of(mockProperty));

                // when & then
                mockMvc.perform(get("/api/properties")
                                .param("regionCode", regionCode))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name").value("Mock Apt"));
        }

        @Test
        @DisplayName("매물 상세 조회 API 호출 성공")
        void getProperty() throws Exception {
                // given
                String propertyId = "1";
                String regionCode = "11110";
                Property mockProperty = new Property(
                                propertyId,
                                "Mock Apt",
                                new Location("Seoul", "Jongno-gu", "Sajik-dong", regionCode),
                                10_000,
                                8_000,
                                2020,
                                84.0);

                given(searchPropertyUseCase.getProperty(propertyId))
                                .willReturn(mockProperty);

                // when & then
                mockMvc.perform(get("/api/properties/{id}", propertyId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(propertyId))
                                .andExpect(jsonPath("$.name").value("Mock Apt"));
        }
}
