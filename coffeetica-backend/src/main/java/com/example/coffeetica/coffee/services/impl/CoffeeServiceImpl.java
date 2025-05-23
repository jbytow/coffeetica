package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.*;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.coffee.specification.CoffeeSpecification;

import com.example.coffeetica.exceptions.ResourceNotFoundException;
import com.example.coffeetica.utility.FileHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link CoffeeService} interface,
 * providing business logic for managing coffees.
 */
@Service
public class CoffeeServiceImpl implements CoffeeService {

    private final CoffeeRepository coffeeRepository;
    private final ReviewRepository reviewRepository;
    private final RoasteryRepository roasteryRepository;
    private final ModelMapper modelMapper;

    /**
     * A file path for uploading coffee images, configured in application.properties,
     * e.g. app.upload.coffees-path=/uploads/coffees/
     */
    @Value("${app.upload.coffees-path}")
    private String coffeesUploadPath;

    /**
     * Constructs a new instance of {@link CoffeeServiceImpl} with the necessary dependencies.
     *
     * @param coffeeRepository the repository for coffee entities
     * @param reviewRepository the repository for review entities
     * @param roasteryRepository the repository for roastery entities
     * @param modelMapper the model mapper for converting entities and DTOs
     */
    public CoffeeServiceImpl(CoffeeRepository coffeeRepository,
                             ReviewRepository reviewRepository,
                             RoasteryRepository roasteryRepository,
                             ModelMapper modelMapper) {
        this.coffeeRepository = coffeeRepository;
        this.reviewRepository = reviewRepository;
        this.roasteryRepository = roasteryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean isCoffeeExists(Long id) {
        return coffeeRepository.existsById(id);
    }

    @Override
    public Page<CoffeeDTO> findCoffees(
            String name,
            String countryOfOrigin,
            Region region,
            RoastLevel roastLevel,
            FlavorProfile flavorProfile,
            Set<String> flavorNotes,
            String processingMethod,
            Integer minProductionYear,
            Integer maxProductionYear,
            String roasteryName,
            Pageable pageable
    ) {
        Specification<CoffeeEntity> spec = CoffeeSpecification.filterByAttributes(
                name, countryOfOrigin, region, roastLevel, flavorProfile, flavorNotes,
                processingMethod, minProductionYear, maxProductionYear, roasteryName
        );

        return coffeeRepository.findAll(spec, pageable)
                .map(entity -> modelMapper.map(entity, CoffeeDTO.class));
    }

    @Override
    public Optional<CoffeeDTO> findCoffeeById(Long id) {
        return coffeeRepository.findById(id)
                .map(entity -> modelMapper.map(entity, CoffeeDTO.class));
    }

    @Override
    public Optional<CoffeeDetailsDTO> findCoffeeDetails(Long coffeeId) {
        return coffeeRepository.findById(coffeeId).map(coffeeEntity -> {
            // Map basic fields
            CoffeeDetailsDTO details = modelMapper.map(coffeeEntity, CoffeeDetailsDTO.class);

            // Retrieve the last 3 reviews
            List<ReviewEntity> latestReviews =
                    reviewRepository.findTop3ByCoffeeIdOrderByCreatedAtDesc(coffeeId);

            List<ReviewDTO> reviewDTOs = latestReviews.stream()
                    .map(reviewEntity -> {
                        ReviewDTO dto = modelMapper.map(reviewEntity, ReviewDTO.class);
                        // Assign any missing relationship attributes
                        dto.setUserId(reviewEntity.getUser().getId());
                        dto.setUserName(reviewEntity.getUser().getUsername());
                        dto.setCoffeeId(reviewEntity.getCoffee().getId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            details.setLatestReviews(reviewDTOs);

            // Calculate average rating
            Double avg = Optional.ofNullable(
                    reviewRepository.findAverageRatingByCoffeeId(coffeeId)
            ).orElse(0.0);
            details.setAverageRating(avg);

            // Count total reviews
            int count = reviewRepository.countByCoffeeId(coffeeId).intValue();
            details.setTotalReviewsCount(count);

            return details;
        });
    }

    @Override
    public Page<CoffeeDTO> findCoffeesByRoasteryId(Long roasteryId,
                                                   int page,
                                                   int size,
                                                   String sortBy,
                                                   String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return coffeeRepository.findByRoasteryId(roasteryId, pageable)
                .map(entity -> modelMapper.map(entity, CoffeeDTO.class));
    }

    @Override
    public CoffeeDetailsDTO findFeaturedCoffee(Long roasteryId) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<CoffeeEntity> page = coffeeRepository.findFeaturedCoffeeByRoasteryId(roasteryId, pageable);
        if (page.hasContent()) {
            Long coffeeId = page.getContent().get(0).getId();
            return findCoffeeDetails(coffeeId).orElse(null);
        }
        return null;
    }

    @Override
    public CoffeeDTO saveCoffee(CoffeeDTO coffeeDTO) {
        CoffeeEntity entity = modelMapper.map(coffeeDTO, CoffeeEntity.class);
        CoffeeEntity savedEntity = coffeeRepository.save(entity);
        return modelMapper.map(savedEntity, CoffeeDTO.class);
    }

    @Override
    public CoffeeDTO updateCoffee(Long id, CoffeeDTO coffeeDTODetails) {
        CoffeeEntity entity = coffeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coffee not found: " + id));

        // If a roastery is specified, ensure it exists
        if (coffeeDTODetails.getRoastery() != null && coffeeDTODetails.getRoastery().getId() != null) {
            RoasteryEntity roastery = roasteryRepository.findById(coffeeDTODetails.getRoastery().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Roastery not found: " + coffeeDTODetails.getRoastery().getId()));
            entity.setRoastery(roastery);
        }

        // Map the rest of the fields from the DTO to the existing entity
        modelMapper.map(coffeeDTODetails, entity);

        CoffeeEntity updatedEntity = coffeeRepository.save(entity);
        return modelMapper.map(updatedEntity, CoffeeDTO.class);
    }

    @Override
    public void deleteCoffee(Long id) {
        CoffeeEntity coffeeEntity = coffeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coffee not found: " + id));

        FileHelper.deleteImage(coffeesUploadPath, coffeeEntity.getImageUrl());
        coffeeRepository.delete(coffeeEntity);
    }

    @Override
    public void updateCoffeeImageUrl(Long id, String newImageUrl) {
        CoffeeEntity coffee = coffeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coffee not found: " + id));

        String oldImageUrl = coffee.getImageUrl();
        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            FileHelper.deleteImage(coffeesUploadPath, oldImageUrl);
        }
        coffee.setImageUrl(newImageUrl);
        coffeeRepository.save(coffee);
    }
}