package com.example.coffeetica.coffee.services;

import java.util.List;

public interface CoffeeOptionsService {
    List<String> getFlavorProfiles();
    List<String> getRegions();
    List<String> getRoastLevels();
}