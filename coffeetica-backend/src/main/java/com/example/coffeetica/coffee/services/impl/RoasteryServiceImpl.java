package com.example.coffeetica.coffee.services.impl;


import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.models.RoasteryEntity;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.services.RoasteryService;
import com.example.coffeetica.coffee.specification.RoasterySpecification;
import com.example.coffeetica.utility.FileHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class RoasteryServiceImpl implements RoasteryService {

    @Autowired
    private RoasteryRepository roasteryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RoasteryDTO> findAllRoasteries() {
        return roasteryRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, RoasteryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoasteryDTO> findFilteredRoasteries(
            String name,
            String country,
            Integer minFoundingYear,
            Integer maxFoundingYear,
            Pageable pageable) {

        Specification<RoasteryEntity> spec = RoasterySpecification.filterByAttributes(
                name, country, minFoundingYear, maxFoundingYear
        );

        return roasteryRepository.findAll(spec, pageable)
                .map(entity -> modelMapper.map(entity, RoasteryDTO.class));
    }

    @Override
    public Optional<RoasteryDTO> findRoasteryById(Long id) {
        return roasteryRepository.findById(id)
                .map(entity -> modelMapper.map(entity, RoasteryDTO.class));
    }

    @Override
    public RoasteryDTO saveRoastery(RoasteryDTO roasteryDTO) {
        RoasteryEntity entity = modelMapper.map(roasteryDTO, RoasteryEntity.class);
        RoasteryEntity savedEntity = roasteryRepository.save(entity);
        return modelMapper.map(savedEntity, RoasteryDTO.class);
    }

    @Override
    public RoasteryDTO updateRoastery(Long id, RoasteryDTO roasteryDetails) {
        RoasteryEntity entity = roasteryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roastery not found"));
        modelMapper.map(roasteryDetails, entity);
        RoasteryEntity updatedEntity = roasteryRepository.save(entity);
        return modelMapper.map(updatedEntity, RoasteryDTO.class);
    }

    @Override
    public void deleteRoastery(Long id) {
        RoasteryEntity roastery = roasteryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roastery not found"));

        // Delete the associated image file using FileHelper
        if (roastery.getImageUrl() != null) {
            FileHelper.deleteImage(roastery.getImageUrl());
        }

        // Delete the roastery entity from the database
        roasteryRepository.deleteById(id);
    }

    @Override
    public boolean isRoasteryExists(Long id) {
        return roasteryRepository.existsById(id);
    }

    @Override
    public void updateRoasteryImageUrl(Long id, String newImageUrl) {
        RoasteryEntity roastery = roasteryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roastery not found"));

        // Check if there is an existing image and if it differs from the new image
        String oldImageUrl = roastery.getImageUrl();
        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            // Use FileHelper to delete the old image
            FileHelper.deleteImage(oldImageUrl);
        }

        // Update the image URL in the roastery entity
        roastery.setImageUrl(newImageUrl);

        // Save the updated roastery entity to the database
        roasteryRepository.save(roastery);
    }
}

