package io.github.jaesungahn.gapinvestor.presentation.controller;

import io.github.jaesungahn.gapinvestor.application.port.in.SearchPropertyUseCase;
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
    public List<Property> search(@RequestParam(required = false, defaultValue = "11110") String regionCode) {
        return searchPropertyUseCase.searchProperties(regionCode);
    }
}
