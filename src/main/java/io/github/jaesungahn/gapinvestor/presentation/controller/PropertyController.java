package io.github.jaesungahn.gapinvestor.presentation.controller;

import io.github.jaesungahn.gapinvestor.application.port.in.SearchPropertyUseCase;
import io.github.jaesungahn.gapinvestor.application.port.in.SortOption;
import io.github.jaesungahn.gapinvestor.domain.property.Property;
import io.github.jaesungahn.gapinvestor.application.port.in.PropertySearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final SearchPropertyUseCase searchPropertyUseCase;

    @GetMapping
    public List<Property> getAllProperties(@RequestParam String regionCode,
            @ModelAttribute PropertySearchCondition condition) {
        return searchPropertyUseCase.searchProperties(regionCode, condition);
    }
}
