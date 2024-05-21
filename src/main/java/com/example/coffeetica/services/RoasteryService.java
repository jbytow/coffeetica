package com.example.coffeetica.services;

import com.example.coffeetica.model.RoasteryDTO;

import java.util.List;
import java.util.Optional;

public interface RoasteryService {

    boolean isRoasteryExists(Long id);

    List<RoasteryDTO> findAllRoasteries();

    Optional<RoasteryDTO> findRoasteryById(Long id);

    RoasteryDTO saveRoastery(RoasteryDTO roasteryDTO);

    RoasteryDTO updateRoastery(Long id, RoasteryDTO roasteryDetails);

    void deleteRoastery(Long id);
}