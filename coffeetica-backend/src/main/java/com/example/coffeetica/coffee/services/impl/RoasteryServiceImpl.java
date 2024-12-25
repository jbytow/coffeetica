package com.example.coffeetica.coffee.services.impl;


import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.models.RoasteryEntity;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.services.RoasteryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        roasteryRepository.deleteById(id);
    }

    @Override
    public boolean isRoasteryExists(Long id) {
        return roasteryRepository.existsById(id);
    }

    @Override
    public void updateImageUrl(Long id, String imageUrl) {
        RoasteryEntity roastery = roasteryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roastery not found"));
        roastery.setImageUrl(imageUrl);
        roasteryRepository.save(roastery);
    }

}

