package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.models.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    Optional<ReviewEntity> findByUserIdAndCoffeeId(Long userId, Long coffeeId);

}
