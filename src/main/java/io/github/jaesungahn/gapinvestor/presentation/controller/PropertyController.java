package io.github.jaesungahn.gapinvestor.presentation.controller;

import io.github.jaesungahn.gapinvestor.application.port.in.SearchPropertyUseCase;
import io.github.jaesungahn.gapinvestor.application.port.in.SortOption;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final SearchPropertyUseCase searchPropertyUseCase;

    public PropertyController(SearchPropertyUseCase searchPropertyUseCase) {
        this.searchPropertyUseCase = searchPropertyUseCase;
    }

    @GetMapping
    public List<Property> getAllProperties(@RequestParam String regionCode,
            @RequestParam(required = false) SortOption sort) {
        return searchPropertyUseCase.searchProperties(regionCode, sort);
    }
}
