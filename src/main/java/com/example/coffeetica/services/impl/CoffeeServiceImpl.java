package com.example.coffeetica.services.impl;

import com.example.coffeetica.model.Coffee;
import com.example.coffeetica.repositories.CoffeeRepository;
import com.example.coffeetica.services.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoffeeServiceImpl implements CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Override
    public List<Coffee> findAllCoffees() {
        return coffeeRepository.findAll();
    }

    @Override
    public Optional<Coffee> findCoffeeById(Long id) {
        return coffeeRepository.findById(id);
    }

    @Override
    public Coffee saveCoffee(Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    @Override
    public Coffee updateCoffee(Long id, Coffee coffeeDetails) {
        Coffee coffee = coffeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Coffee not found"));
        coffee.setName(coffeeDetails.getName());
        coffee.setCountryOfOrigin(coffeeDetails.getCountryOfOrigin());
        coffee.setRegion(coffeeDetails.getRegion());
        coffee.setRoastery(coffeeDetails.getRoastery());
        coffee.setRoastLevel(coffeeDetails.getRoastLevel());
        coffee.setFlavorProfile(coffeeDetails.getFlavorProfile());
        coffee.setNotes(coffeeDetails.getNotes());
        coffee.setProcessingMethod(coffeeDetails.getProcessingMethod());
        coffee.setProductionYear(coffeeDetails.getProductionYear());
        return coffeeRepository.save(coffee);
    }

    @Override
    public boolean isCoffeeExists(Long id) {
        return coffeeRepository.existsById(id);
    }

    @Override
    public void deleteCoffee(Long id) {
        coffeeRepository.deleteById(id);
    }

}
