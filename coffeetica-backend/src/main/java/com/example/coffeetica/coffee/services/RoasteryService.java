package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.RoasteryDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for managing roasteries.
 */
public interface RoasteryService {

    /**
     * Checks if a roastery with the given ID exists.
     *
     * @param id the roastery's ID
     * @return true if it exists, false otherwise
     */
    boolean isRoasteryExists(Long id);

    /**
     * Retrieves all roasteries.
     *
     * @return a list of roasteries
     */
    List<RoasteryDTO> findAllRoasteries();

    /**
     * Retrieves a page of roasteries filtered by various attributes.
     *
     * @param name the name filter
     * @param country the country filter
     * @param minFoundingYear the minimum founding year
     * @param maxFoundingYear the maximum founding year
     * @param pageable the pagination and sorting parameters
     * @return a page of filtered roasteries
     */
    Page<RoasteryDTO> findFilteredRoasteries(
            String name,
            String country,
            Integer minFoundingYear,
            Integer maxFoundingYear,
            Pageable pageable
    );

    /**
     * Finds a roastery by ID.
     *
     * @param id the roastery's ID
     * @return an optional containing the roastery DTO if found, empty otherwise
     */
    Optional<RoasteryDTO> findRoasteryById(Long id);

    /**
     * Creates and saves a new roastery.
     *
     * @param roasteryDTO the roastery data
     * @return the saved roastery DTO
     */
    RoasteryDTO saveRoastery(RoasteryDTO roasteryDTO);

    /**
     * Updates an existing roastery by ID.
     *
     * @param id the roastery's ID
     * @param roasteryDetails the updated roastery data
     * @return the updated roastery DTO
     */
    RoasteryDTO updateRoastery(Long id, RoasteryDTO roasteryDetails);

    /**
     * Deletes a roastery by ID.
     *
     * @param id the roastery's ID
     */
    void deleteRoastery(Long id);

    /**
     * Updates the image URL for a roastery, optionally removing an old image if present.
     *
     * @param id the roastery's ID
     * @param imageUrl the new image URL
     */
    void updateRoasteryImageUrl(Long id, String imageUrl);

}