package com.example.coffeetica.controllers;

import com.example.coffeetica.model.CoffeeDTO;
import com.example.coffeetica.services.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    @GetMapping(path = "/api/coffees/")
    public List<CoffeeDTO> getAllCoffees() {
        return coffeeService.findAllCoffees();
    }

    @GetMapping(path = "/api/coffees/{id}")
    public ResponseEntity<CoffeeDTO> retrieveCoffee(@PathVariable Long id) {
        final Optional<CoffeeDTO> foundCoffee = coffeeService.findCoffeeById(id);
        return foundCoffee
                .map(coffee -> new ResponseEntity<>(coffee, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/api/coffees/")
    public ResponseEntity<CoffeeDTO> createCoffee(@RequestBody CoffeeDTO coffeeDTO) {
        CoffeeDTO savedCoffeeDTO = coffeeService.saveCoffee(coffeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoffeeDTO);
    }

    @PutMapping("/api/coffees/{id}")
    public CoffeeDTO updateCoffee(@PathVariable Long id, @RequestBody CoffeeDTO coffeeDTODetails) {
        return coffeeService.updateCoffee(id, coffeeDTODetails);
    }

    @DeleteMapping("/api/coffees/{id}")
    public ResponseEntity deleteCoffee(@PathVariable Long id) {
        coffeeService.deleteCoffee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
