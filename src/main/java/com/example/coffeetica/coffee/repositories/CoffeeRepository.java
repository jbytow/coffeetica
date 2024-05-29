package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.model.CoffeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeRepository extends JpaRepository<CoffeeEntity, Long> {

}
