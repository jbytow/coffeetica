package com.example.coffeetica.services;

import com.example.coffeetica.model.Coffee;

import java.util.List;
import java.util.Optional;

public interface CoffeeService {

    boolean isCoffeeExists(Long id);

    List<Coffee> findAllCoffees();

    Optional<Coffee> findCoffeeById(Long id);

    Coffee saveCoffee(Coffee coffee);

    Coffee updateCoffee(Long id, Coffee coffeeDetails);

    void deleteCoffee(Long id);
}
