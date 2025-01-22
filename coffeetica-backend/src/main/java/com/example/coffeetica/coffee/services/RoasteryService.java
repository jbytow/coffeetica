package com.example.coffeetica.coffee.services;

import com.example.coffeetica.coffee.models.RoasteryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoasteryService {

    boolean isRoasteryExists(Long id);

    List<RoasteryDTO> findAllRoasteries();

    Page<RoasteryDTO> findFilteredRoasteries(
            String name,
            String country,
            Integer minFoundingYear,
            Integer maxFoundingYear,
            Pageable pageable
    );

    Optional<RoasteryDTO> findRoasteryById(Long id);

    RoasteryDTO saveRoastery(RoasteryDTO roasteryDTO);

    RoasteryDTO updateRoastery(Long id, RoasteryDTO roasteryDetails);

    void deleteRoastery(Long id);

    void updateRoasteryImageUrl(Long id, String imageUrl);

}
