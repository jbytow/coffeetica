package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.coffee.services.RoasteryService;

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

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


/**
 * REST controller for managing roasteries. Provides endpoints for creating,
 * retrieving, updating, and deleting roasteries, as well as uploading images.
 */
@RestController
@RequestMapping("/api/roasteries")
public class RoasteryController {

    private final RoasteryService roasteryService;
    private final CoffeeService coffeeService;

    /**
     * A file path for uploading roastery images, configured in application.properties.
     * For example: app.upload.roasteries-path=uploads/roasteries/
     */
    @Value("${app.upload.roasteries-path:uploads/roasteries/}")
    private String roasteriesUploadPath;

    /**
     * Constructs a new {@link RoasteryController}.
     *
     * @param roasteryService the roastery service
     * @param coffeeService the coffee service
     */
    public RoasteryController(RoasteryService roasteryService, CoffeeService coffeeService) {
        this.roasteryService = roasteryService;
        this.coffeeService = coffeeService;
    }

    /**
     * Retrieves all roasteries without filtering.
     *
     * @return a list of all roasteries
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<RoasteryDTO> getAllRoasteries() {
        return roasteryService.findAllRoasteries();
    }

    /**
     * Retrieves a page of roasteries filtered by optional criteria.
     *
     * @param name roastery name filter
     * @param country country filter
     * @param minFoundingYear minimum founding year filter
     * @param maxFoundingYear maximum founding year filter
     * @param page page index
     * @param size page size
     * @param sortBy sort field
     * @param direction sort direction (asc/desc)
     * @return a page of filtered roasteries
     */
    @GetMapping("/filter")
    @PreAuthorize("permitAll()")
    public Page<RoasteryDTO> getFilteredRoasteries(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer minFoundingYear,
            @RequestParam(required = false) Integer maxFoundingYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return roasteryService.findFilteredRoasteries(name, country, minFoundingYear, maxFoundingYear, pageable);
    }

    /**
     * Retrieves a specific roastery by its ID.
     *
     * @param id the roastery ID
     * @return the roastery data if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<RoasteryDTO> getRoasteryById(@PathVariable Long id) {
        Optional<RoasteryDTO> roasteryDTO = roasteryService.findRoasteryById(id);
        return roasteryDTO
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves all coffees associated with a specific roastery by its ID.
     *
     * @param id the roastery ID
     * @param page page index
     * @param size page size
     * @param sortBy sort field
     * @param direction sort direction (asc/desc)
     * @return a page of coffees if the roastery exists, otherwise 404 Not Found
     */
    @GetMapping("/{id}/coffees")
    @PreAuthorize("permitAll()")
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

    /**
     * Retrieves a featured coffee for a given roastery.
     *
     * @param id the roastery ID
     * @return the featured coffee if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}/featured-coffee")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CoffeeDetailsDTO> getFeaturedCoffee(@PathVariable("id") Long id) {
        CoffeeDetailsDTO featuredCoffee = coffeeService.findFeaturedCoffee(id);
        if (featuredCoffee != null) {
            return ResponseEntity.ok(featuredCoffee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new roastery.
     *
     * @param roasteryDTO the roastery data
     * @return the created roastery with HTTP 201 Created
     */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<RoasteryDTO> createRoastery(@Valid @RequestBody RoasteryDTO roasteryDTO) {
        RoasteryDTO savedRoasteryDTO = roasteryService.saveRoastery(roasteryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoasteryDTO);
    }

    /**
     * Updates an existing roastery by its ID.
     *
     * @param id the roastery ID
     * @param roasteryDetails the updated roastery data
     * @return the updated roastery, or 404 if not found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<RoasteryDTO> updateRoastery(@PathVariable Long id,
                                                      @Valid @RequestBody RoasteryDTO roasteryDetails) {
        try {
            RoasteryDTO updatedRoasteryDTO = roasteryService.updateRoastery(id, roasteryDetails);
            return ResponseEntity.ok(updatedRoasteryDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes a roastery by its ID.
     *
     * @param id the roastery ID
     * @return HTTP 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteRoastery(@PathVariable Long id) {
        roasteryService.deleteRoastery(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Uploads an image file for the specified roastery and updates its image URL.
     * Validates file non-emptiness and checks size as a basic example.
     *
     * @param id the roastery ID
     * @param file the multipart file to upload
     * @return a message indicating success or failure
     */
    @PostMapping("/{id}/upload-image")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> uploadRoasteryImage(@PathVariable Long id,
                                                      @RequestParam("file") MultipartFile file) {
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
            Path uploadPath = Paths.get(roasteriesUploadPath); // read from @Value property
            Files.createDirectories(uploadPath);

            // Save file to disk
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Update database with image URL
            String imageUrl = "roasteries/" + fileName;
            

            roasteryService.updateRoasteryImageUrl(id, imageUrl);

            return ResponseEntity.ok("File uploaded successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }
}