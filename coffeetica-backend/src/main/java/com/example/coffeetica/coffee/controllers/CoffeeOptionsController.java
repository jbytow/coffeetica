package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.services.CoffeeOptionsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class CoffeeOptionsController {

    private final CoffeeOptionsService coffeeOptionsService;

    public CoffeeOptionsController(CoffeeOptionsService coffeeOptionsService) {
        this.coffeeOptionsService = coffeeOptionsService;
    }

    @GetMapping("/api/coffees/options/flavor-profiles")
    @PreAuthorize("permitAll()")
    public List<String> getFlavorProfiles() {
        return coffeeOptionsService.getFlavorProfiles();
    }

    @GetMapping("/api/coffees/options/regions")
    @PreAuthorize("permitAll()")
    public List<String> getRegions() {
        return coffeeOptionsService.getRegions();
    }

    @GetMapping("/api/coffees/options/roast-levels")
    @PreAuthorize("permitAll()")
    public List<String> getRoastLevels() {
        return coffeeOptionsService.getRoastLevels();
    }
}
