package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeRepository extends JpaRepository<CoffeeEntity, Long> {

}
