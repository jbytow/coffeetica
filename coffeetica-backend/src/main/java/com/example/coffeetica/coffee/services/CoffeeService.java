package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.CoffeeDTO;

import com.example.coffeetica.coffee.models.CoffeeDetailsDTO;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface defining operations for managing coffee entities.
 */
public interface CoffeeService {

    /**
     * Checks if a coffee with the given ID exists.
     *
     * @param id the coffee ID
     * @return true if it exists, false otherwise
     */
    boolean isCoffeeExists(Long id);

    /**
     * Finds coffees by various optional attributes (filtering) and returns a paginated result.
     *
     * @param name coffee name (optional)
     * @param countryOfOrigin coffee origin country (optional)
     * @param region region enum (optional)
     * @param roastLevel roast level enum (optional)
     * @param flavorProfile flavor profile enum (optional)
     * @param flavorNotes set of flavor notes (optional)
     * @param processingMethod the method used for coffee processing (optional)
     * @param minProductionYear optional lower bound for production year
     * @param maxProductionYear optional upper bound for production year
     * @param roasteryName optional roastery name filter
     * @param pageable pagination and sorting info
     * @return a page of matching coffees
     */
    Page<CoffeeDTO> findCoffees(
            String name,
            String countryOfOrigin,
            Region region,
            RoastLevel roastLevel,
            FlavorProfile flavorProfile,
            Set<String> flavorNotes,
            String processingMethod,
            Integer minProductionYear,
            Integer maxProductionYear,
            String roasteryName,
            Pageable pageable
    );

    /**
     * Finds a coffee by its ID, returning a basic DTO if found.
     *
     * @param id the coffee ID
     * @return an optional containing the coffee DTO if found, empty otherwise
     */
    Optional<CoffeeDTO> findCoffeeById(Long id);

    /**
     * Finds detailed coffee information, including latest reviews, average rating, etc.
     *
     * @param coffeeId the coffee ID
     * @return an optional containing a detailed coffee DTO if found, empty otherwise
     */
    Optional<CoffeeDetailsDTO> findCoffeeDetails(Long coffeeId);

    /**
     * Finds coffees belonging to a specific roastery by ID, returning a paginated list.
     *
     * @param roasteryId the roastery ID
     * @param page page index
     * @param size page size
     * @param sortBy sort field
     * @param direction sort direction
     * @return a page of coffees for the given roastery
     */
    Page<CoffeeDTO> findCoffeesByRoasteryId(Long roasteryId, int page, int size, String sortBy, String direction);

    /**
     * Finds a "featured" coffee for a roastery (e.g. highest rated).
     *
     * @param roasteryId the roastery ID
     * @return a coffee details DTO for the top coffee, or null if none found
     */
    CoffeeDetailsDTO findFeaturedCoffee(Long roasteryId);

    /**
     * Creates and saves a new coffee.
     *
     * @param coffeeDTO the coffee data to save
     * @return the saved coffee DTO
     */
    CoffeeDTO saveCoffee(CoffeeDTO coffeeDTO);

    /**
     * Updates an existing coffee by its ID.
     *
     * @param id the coffee ID
     * @param coffeeDTODetails the coffee data to update
     * @return the updated coffee DTO
     */
    CoffeeDTO updateCoffee(Long id, CoffeeDTO coffeeDTODetails);

    /**
     * Deletes a coffee by its ID, removing its image if present.
     *
     * @param id the coffee ID
     */
    void deleteCoffee(Long id);

    /**
     * Updates the coffee's image URL, optionally deleting the old image if it differs.
     *
     * @param id the coffee ID
     * @param imageUrl the new image URL
     */
    void updateCoffeeImageUrl(Long id, String imageUrl);
}