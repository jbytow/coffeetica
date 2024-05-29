package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.model.CoffeeDTO;
import com.example.coffeetica.coffee.model.CoffeeEntity;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.services.CoffeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoffeeServiceImpl implements CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<CoffeeDTO> findAllCoffees() {
        return coffeeRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, CoffeeDTO.class))
                .collect(Collectors.toList());
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
        modelMapper.map(coffeeDTODetails, entity);
        CoffeeEntity updatedEntity = coffeeRepository.save(entity);
        return modelMapper.map(updatedEntity, CoffeeDTO.class);
    }

    @Override
    public void deleteCoffee(Long id) {
        coffeeRepository.deleteById(id);
    }

    @Override
    public boolean isCoffeeExists(Long id) {
        return coffeeRepository.existsById(id);
    }
}
