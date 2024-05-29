package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.model.CoffeeDTO;

import java.util.List;
import java.util.Optional;

public interface CoffeeService {

    boolean isCoffeeExists(Long id);

    List<CoffeeDTO> findAllCoffees();

    Optional<CoffeeDTO> findCoffeeById(Long id);

    CoffeeDTO saveCoffee(CoffeeDTO coffeeDTO);

    CoffeeDTO updateCoffee(Long id, CoffeeDTO coffeeDTODetails);

    void deleteCoffee(Long id);
}
