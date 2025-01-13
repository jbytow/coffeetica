package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.services.CoffeeOptionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coffees/options")
public class CoffeeOptionsController {

    private final CoffeeOptionsService coffeeOptionsService;

    public CoffeeOptionsController(CoffeeOptionsService coffeeOptionsService) {
        this.coffeeOptionsService = coffeeOptionsService;
    }

    @GetMapping("/flavor-profiles")
    public List<String> getFlavorProfiles() {
        return coffeeOptionsService.getFlavorProfiles();
    }

    @GetMapping("/regions")
    public List<String> getRegions() {
        return coffeeOptionsService.getRegions();
    }

    @GetMapping("/roast-levels")
    public List<String> getRoastLevels() {
        return coffeeOptionsService.getRoastLevels();
    }
}
