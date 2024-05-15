package com.example.coffeetica.repositories;

import com.example.coffeetica.model.RoasteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoasteryRepository extends JpaRepository<RoasteryEntity, Long> {

}
