package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.models.CoffeeEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for managing {@link CoffeeEntity} persistence.
 * Extends JPA repositories for CRUD operations, and JPA Specification Executor
 * for custom filtering.
 */
@Repository
public interface CoffeeRepository extends JpaRepository<CoffeeEntity, Long>, JpaSpecificationExecutor<CoffeeEntity> {

    /**
     * Retrieves all coffees in a paginated format.
     *
     * @param pageable the pagination and sorting parameters
     * @return a page of coffees
     */
    Page<CoffeeEntity> findAll(Pageable pageable);

    /**
     * Retrieves coffees belonging to a specific roastery.
     *
     * @param roasteryId the ID of the roastery
     * @param pageable the pagination and sorting parameters
     * @return a page of coffees for the roastery
     */
    Page<CoffeeEntity> findByRoasteryId(Long roasteryId, Pageable pageable);

    /**
     * Retrieves coffees for a specific roastery, sorted by the average rating (descending).
     *
     * @param roasteryId the ID of the roastery
     * @param pageable pagination and sorting information
     * @return a page of coffees, highest average rating first
     */
    @Query("""
        SELECT c FROM CoffeeEntity c LEFT JOIN c.reviews r
        WHERE c.roastery.id = :roasteryId
        GROUP BY c.id
        HAVING COUNT(r.id) > 0
        ORDER BY AVG(r.rating) DESC
    """)
    Page<CoffeeEntity> findFeaturedCoffeeByRoasteryId(@Param("roasteryId") Long roasteryId, Pageable pageable);
}
