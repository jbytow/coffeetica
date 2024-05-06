package com.example.coffeetica.controllers;

import com.example.coffeetica.model.Coffee;
import com.example.coffeetica.services.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coffees/")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    @GetMapping
    public List<Coffee> getAllCoffees() {
        return coffeeService.findAllCoffees();
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Coffee> retrieveCoffee(@PathVariable Long id) {
        final Optional<Coffee> foundCoffee = coffeeService.findCoffeeById(id);
        return foundCoffee
                .map(coffee -> new ResponseEntity<>(coffee, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Coffee> createCoffee(@RequestBody Coffee coffee) {
        Coffee savedCoffee = coffeeService.saveCoffee(coffee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoffee);
    }

    @PutMapping("{id}")
    public Coffee updateCoffee(@PathVariable Long id, @RequestBody Coffee coffeeDetails) {
        return coffeeService.updateCoffee(id, coffeeDetails);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCoffee(@PathVariable Long id) {
        coffeeService.deleteCoffee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
