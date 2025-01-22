package com.example.coffeetica.coffee.repositories;

import com.example.coffeetica.coffee.models.RoasteryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoasteryRepository extends JpaRepository<RoasteryEntity, Long>, JpaSpecificationExecutor<RoasteryEntity> {

    Page<RoasteryEntity> findAll(Pageable pageable);

}
