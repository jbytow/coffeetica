package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CoffeeService {

    boolean isCoffeeExists(Long id);

    Page<CoffeeDTO> findAllCoffees(Pageable pageable);

    Optional<CoffeeDTO> findCoffeeById(Long id);

    CoffeeDTO saveCoffee(CoffeeDTO coffeeDTO);

    CoffeeDTO updateCoffee(Long id, CoffeeDTO coffeeDTODetails);

    void deleteCoffee(Long id);

    void updateCoffeeImageUrl(Long id, String imageUrl);
}
