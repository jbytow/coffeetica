package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.models.CoffeeEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeRepository extends JpaRepository<CoffeeEntity, Long>, JpaSpecificationExecutor<CoffeeEntity> {

    Page<CoffeeEntity> findAll(Pageable pageable);

    Page<CoffeeEntity> findByRoasteryId(Long roasteryId, Pageable pageable);

    @Query("SELECT c FROM CoffeeEntity c LEFT JOIN c.reviews r " +
            "WHERE c.roastery.id = :roasteryId " +
            "GROUP BY c.id " +
            "ORDER BY AVG(r.rating) DESC")
    Page<CoffeeEntity> findFeaturedCoffeeByRoasteryId(@Param("roasteryId") Long roasteryId, Pageable pageable);

}
