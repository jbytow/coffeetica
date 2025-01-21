package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.CoffeeDTO;

import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoffeeService {

    boolean isCoffeeExists(Long id);

    Page<CoffeeDTO> findAllCoffees(Pageable pageable);

    Page<CoffeeDTO> findFilteredCoffees(
            String name,
            String countryOfOrigin,
            Region region,
            RoastLevel roastLevel,
            FlavorProfile flavorProfile,
            Set<String> flavorNotes,
            String processingMethod,
            Integer minProductionYear,
            Integer maxProductionYear,
            String roasteryName,
            Pageable pageable
    );

    Optional<CoffeeDTO> findCoffeeById(Long id);

    CoffeeDTO saveCoffee(CoffeeDTO coffeeDTO);

    CoffeeDTO updateCoffee(Long id, CoffeeDTO coffeeDTODetails);

    void deleteCoffee(Long id);

    void updateCoffeeImageUrl(Long id, String imageUrl);
}
