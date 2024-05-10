package com.example.coffeetica.services.impl;

import com.example.coffeetica.model.CoffeeDTO;
import com.example.coffeetica.repositories.CoffeeRepository;
import com.example.coffeetica.services.CoffeeService;
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
        return coffeeRepository.findById(id);
    }

    @Override
    public CoffeeDTO saveCoffee(CoffeeDTO coffeeDTO) {
        return coffeeRepository.save(coffeeDTO);
    }

    @Override
    public CoffeeDTO updateCoffee(Long id, CoffeeDTO coffeeDTODetails) {
        CoffeeDTO coffeeDTO = coffeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Coffee not found"));
        coffeeDTO.setName(coffeeDTODetails.getName());
        coffeeDTO.setCountryOfOrigin(coffeeDTODetails.getCountryOfOrigin());
        coffeeDTO.setRegion(coffeeDTODetails.getRegion());
        coffeeDTO.setRoastery(coffeeDTODetails.getRoastery());
        coffeeDTO.setRoastLevel(coffeeDTODetails.getRoastLevel());
        coffeeDTO.setFlavorProfile(coffeeDTODetails.getFlavorProfile());
        coffeeDTO.setNotes(coffeeDTODetails.getNotes());
        coffeeDTO.setProcessingMethod(coffeeDTODetails.getProcessingMethod());
        coffeeDTO.setProductionYear(coffeeDTODetails.getProductionYear());
        return coffeeRepository.save(coffeeDTO);
    }

    @Override
    public boolean isCoffeeExists(Long id) {
        return coffeeRepository.existsById(id);
    }

    @Override
    public void deleteCoffee(Long id) {
        coffeeRepository.deleteById(id);
    }

}
