package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.models.RoasteryEntity;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.services.RoasteryService;
import com.example.coffeetica.coffee.specification.RoasterySpecification;
import com.example.coffeetica.exceptions.ResourceNotFoundException;
import com.example.coffeetica.utility.FileHelper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Implementation of the {@link RoasteryService} interface,
 * providing business logic for managing roasteries.
 */
@Service
public class RoasteryServiceImpl implements RoasteryService {

    private final RoasteryRepository roasteryRepository;
    private final ModelMapper modelMapper;

    /**
     * A file path for uploading roastery images, configured in application.properties.
     * For example: app.upload.roasteries-path=/uploads/roasteries/
     */
    @Value("${app.upload.roasteries-path/}")
    private String roasteriesUploadPath;

    /**
     * Constructs a new instance of {@link RoasteryServiceImpl}.
     *
     * @param roasteryRepository the repository for roastery entities
     * @param modelMapper the model mapper for converting entities and DTOs
     */
    public RoasteryServiceImpl(RoasteryRepository roasteryRepository, ModelMapper modelMapper) {
        this.roasteryRepository = roasteryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean isRoasteryExists(Long id) {
        return roasteryRepository.existsById(id);
    }

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
            Pageable pageable
    ) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Roastery not found with ID: " + id));

        // Map fields from the incoming DTO to the existing entity
        modelMapper.map(roasteryDetails, entity);

        RoasteryEntity updatedEntity = roasteryRepository.save(entity);
        return modelMapper.map(updatedEntity, RoasteryDTO.class);
    }

    @Override
    public void deleteRoastery(Long id) {
        RoasteryEntity roastery = roasteryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roastery not found with ID: " + id));

        // Delete the associated image file, if any
        if (roastery.getImageUrl() != null) {
            FileHelper.deleteImage(roasteriesUploadPath, roastery.getImageUrl());
        }

        roasteryRepository.deleteById(id);
    }

    @Override
    public void updateRoasteryImageUrl(Long id, String newImageUrl) {
        RoasteryEntity roastery = roasteryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roastery not found with ID: " + id));

        String oldImageUrl = roastery.getImageUrl();
        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            FileHelper.deleteImage(roasteriesUploadPath, oldImageUrl);
        }

        roastery.setImageUrl(newImageUrl);
        roasteryRepository.save(roastery);
    }
}

