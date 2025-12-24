package io.github.jaesungahn.gapinvestor.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddBookmarkRequest {
    @NotBlank
    private String propertyId;

    @NotBlank
    private String regionCode;

    @NotBlank
    private String dong;

    @NotBlank
    private String apartmentName;

    private String jibun;

    @NotNull
    private Integer buildYear;

    @NotNull
    private Double exclusiveArea;

    @NotNull
    private Long salePrice; // Current price needed for snapshot

    @NotNull
    private Long jeonsePrice; // Current price needed for snapshot
}
