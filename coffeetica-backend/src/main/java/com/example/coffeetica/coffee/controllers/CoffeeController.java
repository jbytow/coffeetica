package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.coffee.services.CoffeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;



    @GetMapping("/api/coffees")
    public Page<CoffeeDTO> getCoffees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String countryOfOrigin,
            @RequestParam(required = false) Region region,
            @RequestParam(required = false) RoastLevel roastLevel,
            @RequestParam(required = false) FlavorProfile flavorProfile,
            @RequestParam(required = false) Set<String> flavorNotes,
            @RequestParam(required = false) String processingMethod,
            @RequestParam(required = false) Integer minProductionYear,
            @RequestParam(required = false) Integer maxProductionYear,
            @RequestParam(required = false) String roasteryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return coffeeService.findCoffees(
                name, countryOfOrigin, region, roastLevel, flavorProfile, flavorNotes,
                processingMethod, minProductionYear, maxProductionYear, roasteryName, pageable
        );
    }

    @GetMapping(path = "/api/coffees/{id}")
    public ResponseEntity<CoffeeDetailsDTO> getCoffeeDetails(@PathVariable Long id) {
        Optional<CoffeeDetailsDTO> detailsOpt = coffeeService.findCoffeeDetails(id);
        return detailsOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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

// Old Methods:

    //    @GetMapping(path = "/api/coffees/{id}")
//    public ResponseEntity<CoffeeDTO> retrieveCoffee(@PathVariable Long id) {
//        final Optional<CoffeeDTO> foundCoffee = coffeeService.findCoffeeById(id);
//        return foundCoffee
//                .map(coffee -> new ResponseEntity<>(coffee, HttpStatus.OK))
//                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }

    //    @GetMapping(path = "/api/coffees")
//    public Page<CoffeeDTO> getAllCoffees(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "desc") String direction) {
//        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//        return coffeeService.findAllCoffees(pageable);
//    }

}
