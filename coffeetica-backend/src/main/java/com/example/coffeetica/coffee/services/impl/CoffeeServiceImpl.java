package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.RoasteryEntity;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.services.CoffeeService;
import com.example.coffeetica.coffee.specification.CoffeeSpecification;

import com.example.coffeetica.utility.FileHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.Set;

@Service
public class CoffeeServiceImpl implements CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private RoasteryRepository roasteryRepository;

    @Autowired
    private ModelMapper modelMapper;

//    @Override
//    public Page<CoffeeDTO> findAllCoffees(Pageable pageable) {
//        return coffeeRepository.findAll(pageable)
//                .map(entity -> modelMapper.map(entity, CoffeeDTO.class));
//    }

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
