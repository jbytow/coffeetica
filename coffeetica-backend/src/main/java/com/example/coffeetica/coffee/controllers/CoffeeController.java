package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.coffee.services.CoffeeService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;


/**
 * REST controller for managing coffee entities. Provides endpoints for
 * creating, updating, deleting, uploading images, and retrieving coffee data.
 */
@RestController
@RequestMapping("/api/coffees")
public class CoffeeController {

    private final CoffeeService coffeeService;

    /**
     * A file path for uploading coffee images, configured in application.properties,
     * e.g. app.upload.coffees-path=/uploads/coffees/
     */
    @Value("${app.upload.coffees-path}")
    private String coffeesUploadPath;

    /**
     * Constructs a new {@link CoffeeController}.
     *
     * @param coffeeService the coffee service
     */
    public CoffeeController(CoffeeService coffeeService) {
        this.coffeeService = coffeeService;
    }

    /**
     * Retrieves a page of coffees filtered by optional criteria.
     *
     * @param name coffee name filter (optional)
     * @param countryOfOrigin coffee origin country filter (optional)
     * @param region coffee region (optional)
     * @param roastLevel coffee roast level (optional)
     * @param flavorProfile coffee flavor profile (optional)
     * @param flavorNotes set of flavor notes (optional)
     * @param processingMethod coffee processing method (optional)
     * @param minProductionYear min production year (optional)
     * @param maxProductionYear max production year (optional)
     * @param roasteryName roastery name filter (optional)
     * @param page page index
     * @param size page size
     * @param sortBy sort field
     * @param direction sort direction (asc/desc)
     * @return a page of matching coffees
     */
    @GetMapping
    @PreAuthorize("permitAll()")
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
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return coffeeService.findCoffees(
                name,
                countryOfOrigin,
                region,
                roastLevel,
                flavorProfile,
                flavorNotes,
                processingMethod,
                minProductionYear,
                maxProductionYear,
                roasteryName,
                pageable
        );
    }

    /**
     * Retrieves detailed information about a specific coffee by its ID.
     *
     * @param id the coffee ID
     * @return a {@link CoffeeDetailsDTO} with extended info, or 404 Not Found if missing
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CoffeeDetailsDTO> getCoffeeDetails(@PathVariable Long id) {
        Optional<CoffeeDetailsDTO> detailsOpt = coffeeService.findCoffeeDetails(id);
        return detailsOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Creates a new coffee. Only Admin users can perform this operation.
     *
     * @param coffeeDTO the coffee data (validated)
     * @return the created coffee DTO, with a 201 Created status
     */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<CoffeeDTO> createCoffee(@Valid @RequestBody CoffeeDTO coffeeDTO) {
        CoffeeDTO savedCoffeeDTO = coffeeService.saveCoffee(coffeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoffeeDTO);
    }

    /**
     * Updates an existing coffee by its ID. Only Admin users can perform this operation.
     *
     * @param id the coffee ID
     * @param coffeeDTODetails updated coffee fields (validated)
     * @return the updated coffee DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<CoffeeDTO> updateCoffee(@PathVariable Long id,
                                                  @Valid @RequestBody CoffeeDTO coffeeDTODetails) {
        try {
            CoffeeDTO updatedCoffee = coffeeService.updateCoffee(id, coffeeDTODetails);
            return ResponseEntity.ok(updatedCoffee);
        } catch (Exception e) {
            // For a ResourceNotFoundException, your GlobalExceptionHandler can produce 404,
            // or you can manually handle it here:
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes a coffee by its ID. Only Admin users can perform this operation.
     *
     * @param id the coffee ID
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteCoffee(@PathVariable Long id) {
        coffeeService.deleteCoffee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Uploads an image file for the specified coffee, updating its image URL.
     * Validates that the file is non-empty, and only Admin can perform this operation.
     *
     * @param id the coffee ID
     * @param file the multipart file to upload
     * @return 200 OK with a success message or 400/500 on failure
     */
    @PostMapping("/{id}/upload-image")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> uploadCoffeeImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }

            // Example file size check (e.g., limit to ~5MB)
            if (file.getSize() > 5_000_000) {
                return ResponseEntity.badRequest().body("File too large");
            }

            // Generate a unique file name
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(coffeesUploadPath);
            Files.createDirectories(uploadPath);

            // Save file to disk
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Update DB with image URL
            String imageUrl = "coffees/" + fileName;

            coffeeService.updateCoffeeImageUrl(id, imageUrl);

            return ResponseEntity.ok("File uploaded successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }
}
