package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.models.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

}
