package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.models.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    Page<ReviewEntity> findByCoffeeId(Long coffeeId, Pageable pageable);

    Optional<ReviewEntity> findByUserIdAndCoffeeId(Long userId, Long coffeeId);

    List<ReviewEntity> findTop3ByCoffeeIdOrderByCreatedAtDesc(Long coffeeId);

    Long countByCoffeeId(Long coffeeId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.coffee.id = :coffeeId")
    Double findAverageRatingByCoffeeId(@Param("coffeeId") Long coffeeId);

}
