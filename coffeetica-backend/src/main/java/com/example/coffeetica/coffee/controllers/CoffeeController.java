package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.services.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@RestController
@RequestMapping
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    @GetMapping(path = "/api/coffees")
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

    @PostMapping(path = "/api/coffees")
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

    @PostMapping("/api/coffees/{id}/upload-image")
    public ResponseEntity<String> uploadCoffeeImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }

            // Generate unique file name
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/coffees/");
            Files.createDirectories(uploadPath);

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Update database with image URL
            String imageUrl = "/uploads/coffees/" + fileName;
            coffeeService.updateCoffeeImageUrl(id, imageUrl);

            return ResponseEntity.ok("File uploaded successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }
}
