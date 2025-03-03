package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.coffee.services.RoasteryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class RoasteryController {

    @Autowired
    private RoasteryService roasteryService;

    @Autowired
    private CoffeeService coffeeService;

    @GetMapping("/api/roasteries")
    public List<RoasteryDTO> getAllRoasteries() {
        return roasteryService.findAllRoasteries();
    }

    @GetMapping("/api/roasteries/filter")
    public Page<RoasteryDTO> getFilteredRoasteries(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer minFoundingYear,
            @RequestParam(required = false) Integer maxFoundingYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return roasteryService.findFilteredRoasteries(name, country, minFoundingYear, maxFoundingYear, pageable);
    }

    @GetMapping("/api/roasteries/{id}")
    public ResponseEntity<RoasteryDTO> getRoasteryById(@PathVariable Long id) {
        Optional<RoasteryDTO> roasteryDTO = roasteryService.findRoasteryById(id);
        return roasteryDTO
                .map(roastery -> new ResponseEntity<>(roastery, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/roasteries/{id}/coffees")
    public ResponseEntity<Page<CoffeeDTO>> getAllCoffeesByRoasteryId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        if (!roasteryService.isRoasteryExists(id)) {
            return ResponseEntity.notFound().build();
        }

        Page<CoffeeDTO> coffees = coffeeService.findCoffeesByRoasteryId(id, page, size, sortBy, direction);
        return ResponseEntity.ok(coffees);
    }

    @GetMapping("/api/roasteries/{id}/featured-coffee")
    public ResponseEntity<CoffeeDetailsDTO> getFeaturedCoffee(@PathVariable("id") Long id) {
        CoffeeDetailsDTO featuredCoffee = coffeeService.findFeaturedCoffee(id);
        if (featuredCoffee != null) {
            return ResponseEntity.ok(featuredCoffee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/roasteries")
    public ResponseEntity<RoasteryDTO> createRoastery(@RequestBody RoasteryDTO roasteryDTO) {
        RoasteryDTO savedRoasteryDTO = roasteryService.saveRoastery(roasteryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoasteryDTO);
    }

    @PutMapping("/api/roasteries/{id}")
    public ResponseEntity<RoasteryDTO> updateRoastery(@PathVariable Long id, @RequestBody RoasteryDTO roasteryDetails) {
        try {
            RoasteryDTO updatedRoasteryDTO = roasteryService.updateRoastery(id, roasteryDetails);
            return ResponseEntity.ok(updatedRoasteryDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/api/roasteries/{id}")
    public ResponseEntity deleteRoastery(@PathVariable Long id) {
        roasteryService.deleteRoastery(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping("/api/roasteries/{id}/upload-image")
    public ResponseEntity<String> uploadRoasteryImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }

            // Generate unique file name
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/roasteries/");
            Files.createDirectories(uploadPath);

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Update database with image URL
            String imageUrl = "/uploads/roasteries/" + fileName;
            roasteryService.updateRoasteryImageUrl(id, imageUrl);

            return ResponseEntity.ok("File uploaded successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }
}
