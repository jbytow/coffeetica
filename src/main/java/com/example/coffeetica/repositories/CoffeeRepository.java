package com.example.coffeetica.repositories;

import com.example.coffeetica.model.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeRepository extends JpaRepository <Coffee, Long> {

}
