package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.coffee.services.CoffeeOptionsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CoffeeOptionsServiceImpl implements CoffeeOptionsService {

    @Override
    public List<String> getFlavorProfiles() {
        return Arrays.stream(FlavorProfile.values())
                .map(FlavorProfile::getDisplayName)
                .toList();
    }

    @Override
    public List<String> getRegions() {
        return Arrays.stream(Region.values())
                .map(Region::getDisplayName)
                .toList();
    }

    @Override
    public List<String> getRoastLevels() {
        return Arrays.stream(RoastLevel.values())
                .map(RoastLevel::getDisplayName)
                .toList();
    }
}