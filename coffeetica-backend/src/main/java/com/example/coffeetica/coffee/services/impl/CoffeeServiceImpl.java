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

import com.example.coffeetica.utility.FileHelper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoffeeServiceImpl implements CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RoasteryRepository roasteryRepository;

    @Autowired
    private ModelMapper modelMapper;

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
            Pageable pageable) {

        Specification<CoffeeEntity> spec = CoffeeSpecification.filterByAttributes(
                name, countryOfOrigin, region, roastLevel, flavorProfile, flavorNotes,
                processingMethod, minProductionYear, maxProductionYear, roasteryName
        );

        return coffeeRepository.findAll(spec, pageable).map(entity -> modelMapper.map(entity, CoffeeDTO.class));
    }

    @Override
    public Optional<CoffeeDTO> findCoffeeById(Long id) {
        return coffeeRepository.findById(id)
                .map(entity -> modelMapper.map(entity, CoffeeDTO.class));
    }

    @Override
    public Optional<CoffeeDetailsDTO> findCoffeeDetails(Long coffeeId) {
        return coffeeRepository.findById(coffeeId)
                .map(coffeeEntity -> {
                    // Map all basic fields
                    CoffeeDetailsDTO details = modelMapper.map(coffeeEntity, CoffeeDetailsDTO.class);

                    // Manually populate additional fields

                    // Retrieve the last 3 reviews
                    List<ReviewEntity> latestReviews =
                            reviewRepository.findTop3ByCoffeeIdOrderByCreatedAtDesc(coffeeId);

                    List<ReviewDTO> reviewDTOs = latestReviews.stream()
                            .map(reviewEntity -> {
                                ReviewDTO dto = modelMapper.map(reviewEntity, ReviewDTO.class);
                                // Assign missing attributes
                                dto.setUserId(reviewEntity.getUser().getId());
                                dto.setUserName(reviewEntity.getUser().getUsername());
                                dto.setCoffeeId(reviewEntity.getCoffee().getId());
                                return dto;
                            })
                            .collect(Collectors.toList());
                    details.setLatestReviews(reviewDTOs);

                    // Calculate the average rating
                    Double avg = Optional.ofNullable(
                            reviewRepository.findAverageRatingByCoffeeId(coffeeId)
                    ).orElse(0.0);
                    details.setAverageRating(avg);

                    // Count the total number of reviews
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
        // sorting and pagination
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // repository
        Page<CoffeeEntity> coffeeEntities =
                coffeeRepository.findByRoasteryId(roasteryId, pageable);

        // mapping
        return coffeeEntities.map(entity -> modelMapper.map(entity, CoffeeDTO.class));
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
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        // roastery field
        if (coffeeDTODetails.getRoastery() != null && coffeeDTODetails.getRoastery().getId() != null) {
            RoasteryEntity roastery = roasteryRepository.findById(coffeeDTODetails.getRoastery().getId())
                    .orElseThrow(() -> new RuntimeException("Roastery not found"));
            entity.setRoastery(roastery);
        }

        modelMapper.map(coffeeDTODetails, entity);
        CoffeeEntity updatedEntity = coffeeRepository.save(entity);
        return modelMapper.map(updatedEntity, CoffeeDTO.class);
    }

    @Override
    public void deleteCoffee(Long id) {
        CoffeeEntity coffeeEntity = coffeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        FileHelper.deleteImage(coffeeEntity.getImageUrl());
        coffeeRepository.delete(coffeeEntity);
    }

    @Override
    public boolean isCoffeeExists(Long id) {
        return coffeeRepository.existsById(id);
    }

    @Override
    public void updateCoffeeImageUrl(Long id, String newImageUrl) {
        CoffeeEntity coffee = coffeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        // Check if there is an existing image and if it differs from the new image
        String oldImageUrl = coffee.getImageUrl();
        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            // Use FileHelper to delete the old image
            FileHelper.deleteImage(oldImageUrl);
        }

        // Update the image URL in the coffee entity
        coffee.setImageUrl(newImageUrl);

        // Save the updated coffee entity to the database
        coffeeRepository.save(coffee);
    }
}
