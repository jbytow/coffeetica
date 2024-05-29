package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.model.RoasteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoasteryRepository extends JpaRepository<RoasteryEntity, Long> {

}
