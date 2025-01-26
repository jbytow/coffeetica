package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoffeeServiceImplTest {

    @Mock
    private CoffeeRepository coffeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CoffeeServiceImpl underTest;

    @Test
    public void testThatCoffeeIsSaved() {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        CoffeeEntity coffeeEntity = CoffeeTestData.createTestCoffeeEntity();

        // Mocking the behavior of ModelMapper
        when(modelMapper.map(coffeeDTO, CoffeeEntity.class)).thenReturn(coffeeEntity);
        when(coffeeRepository.save(coffeeEntity)).thenReturn(coffeeEntity);
        when(modelMapper.map(coffeeEntity, CoffeeDTO.class)).thenReturn(coffeeDTO);

        // Action
        CoffeeDTO result = underTest.saveCoffee(coffeeDTO);

        // Assertions
        assertEquals(coffeeDTO, result);
        verify(modelMapper).map(coffeeDTO, CoffeeEntity.class);
        verify(coffeeRepository).save(coffeeEntity);
        verify(modelMapper).map(coffeeEntity, CoffeeDTO.class);
    }

    @Test
    public void testThatFindByIdReturnsCoffeeWhenExists() {
        Long id = 1L;
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        CoffeeEntity coffeeEntity = CoffeeTestData.createTestCoffeeEntity();

        when(coffeeRepository.findById(id)).thenReturn(Optional.of(coffeeEntity));
        when(modelMapper.map(coffeeEntity, CoffeeDTO.class)).thenReturn(coffeeDTO);

        Optional<CoffeeDTO> result = underTest.findCoffeeById(id);

        assertTrue(result.isPresent());
        assertEquals(coffeeDTO, result.get());
        verify(coffeeRepository).findById(id);
        verify(modelMapper).map(coffeeEntity, CoffeeDTO.class);
    }

    @Test
    public void testThatFindByIdReturnEmptyWhenNoCoffee() {
        Long id = 1L;
        when(coffeeRepository.findById(id)).thenReturn(Optional.empty());

        Optional<CoffeeDTO> result = underTest.findCoffeeById(id);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testListCoffeesReturnsEmptyListWhenNoCoffeesExist() {
        Page<CoffeeEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(coffeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        Pageable pageable = PageRequest.of(0, 5);
        String name = null;
        String countryOfOrigin = null;
        Region region = null;
        RoastLevel roastLevel = null;
        FlavorProfile flavorProfile = null;
        Set<String> flavorNotes = null;
        String processingMethod = null;
        Integer minProductionYear = null;
        Integer maxProductionYear = null;
        String roasteryName = null;

        Page<CoffeeDTO> result = underTest.findCoffees(
                name, countryOfOrigin, region, roastLevel, flavorProfile, flavorNotes,
                processingMethod, minProductionYear, maxProductionYear, roasteryName, pageable
        );

        assertTrue(result.isEmpty());
        verify(coffeeRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testListCoffeesReturnsCoffeesWhenExist() {
        CoffeeDTO coffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        CoffeeEntity coffeeEntity = CoffeeTestData.createTestCoffeeEntity();

        Page<CoffeeEntity> coffeePage = new PageImpl<>(List.of(coffeeEntity));
        when(coffeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(coffeePage);
        when(modelMapper.map(coffeeEntity, CoffeeDTO.class)).thenReturn(coffeeDTO);

        Pageable pageable = PageRequest.of(0, 5);
        String name = null;
        String countryOfOrigin = null;
        Region region = null;
        RoastLevel roastLevel = null;
        FlavorProfile flavorProfile = null;
        Set<String> flavorNotes = null;
        String processingMethod = null;
        Integer minProductionYear = null;
        Integer maxProductionYear = null;
        String roasteryName = null;


        Page<CoffeeDTO> result = underTest.findCoffees(
                name, countryOfOrigin, region, roastLevel, flavorProfile, flavorNotes,
                processingMethod, minProductionYear, maxProductionYear, roasteryName, pageable
        );


        assertEquals(1, result.getContent().size());
        assertEquals(coffeeDTO, result.getContent().get(0));
        verify(modelMapper).map(coffeeEntity, CoffeeDTO.class);

        verify(coffeeRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testCoffeeExistsReturnsFalseWhenCoffeeDoesntExist() {
        Long id = 1L;
        when(coffeeRepository.existsById(id)).thenReturn(false);

        boolean result = underTest.isCoffeeExists(id);

        assertFalse(result);
    }

    @Test
    public void testCoffeeExistsReturnsTrueWhenCoffeeDoesExist() {
        Long id = 1L;
        when(coffeeRepository.existsById(id)).thenReturn(true);

        boolean result = underTest.isCoffeeExists(id);

        assertTrue(result);
    }

    @Test
    public void testThatDeleteCoffeeDeletesCoffee() {
        Long id = 1L;
        CoffeeEntity coffeeEntity = CoffeeTestData.createTestCoffeeEntity();

        when(coffeeRepository.findById(id)).thenReturn(Optional.of(coffeeEntity));

        underTest.deleteCoffee(id);

        verify(coffeeRepository, times(1)).delete(coffeeEntity);
    }
}
