package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeDTO;
import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.RoasteryEntity;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import com.example.coffeetica.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
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






/**
 * Unit tests for {@link CoffeeServiceImpl} verifying CRUD operations
 * and ResourceNotFoundException handling, analogous to RoasteryServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
public class CoffeeServiceImplTest {

    @Mock
    private CoffeeRepository coffeeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RoasteryRepository roasteryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CoffeeServiceImpl coffeeService;

    private CoffeeDTO sampleCoffeeDTO;
    private CoffeeEntity sampleCoffeeEntity;

    /**
     * Initializes reusable test data before each test method.
     */
    @BeforeEach
    void setUp() {
        sampleCoffeeDTO = CoffeeTestData.createTestCoffeeDTO();
        sampleCoffeeEntity = CoffeeTestData.createTestCoffeeEntity();
    }

    /**
     * Tests that saving a new coffee properly maps DTO to entity,
     * calls the repository's save(), and maps the entity back to DTO.
     */
    @Test
    public void testThatCoffeeIsSaved() {
        when(modelMapper.map(sampleCoffeeDTO, CoffeeEntity.class)).thenReturn(sampleCoffeeEntity);
        when(coffeeRepository.save(sampleCoffeeEntity)).thenReturn(sampleCoffeeEntity);
        when(modelMapper.map(sampleCoffeeEntity, CoffeeDTO.class)).thenReturn(sampleCoffeeDTO);

        CoffeeDTO result = coffeeService.saveCoffee(sampleCoffeeDTO);

        assertEquals(sampleCoffeeDTO, result);
        verify(modelMapper).map(sampleCoffeeDTO, CoffeeEntity.class);
        verify(coffeeRepository).save(sampleCoffeeEntity);
        verify(modelMapper).map(sampleCoffeeEntity, CoffeeDTO.class);
    }

    /**
     * Tests that finding a coffee by its ID returns a matching DTO
     * if the coffee entity is present in the database.
     */
    @Test
    public void testThatFindByIdReturnsCoffeeWhenExists() {
        Long id = 1L;
        when(coffeeRepository.findById(id)).thenReturn(Optional.of(sampleCoffeeEntity));
        when(modelMapper.map(sampleCoffeeEntity, CoffeeDTO.class)).thenReturn(sampleCoffeeDTO);

        Optional<CoffeeDTO> result = coffeeService.findCoffeeById(id);

        assertTrue(result.isPresent());
        assertEquals(sampleCoffeeDTO, result.get());
        verify(coffeeRepository).findById(id);
        verify(modelMapper).map(sampleCoffeeEntity, CoffeeDTO.class);
    }

    /**
     * Tests that finding a coffee by a non-existent ID returns an empty Optional.
     */
    @Test
    public void testThatFindByIdReturnsEmptyWhenNoCoffee() {
        Long id = 999L;
        when(coffeeRepository.findById(id)).thenReturn(Optional.empty());

        Optional<CoffeeDTO> result = coffeeService.findCoffeeById(id);

        assertFalse(result.isPresent());
        verify(coffeeRepository).findById(id);
        verifyNoInteractions(modelMapper);
    }

    /**
     * Tests that listing coffees returns an empty page if none exist.
     */
    @Test
    public void testListCoffeesReturnsEmptyPageWhenNoCoffeesExist() {
        Page<CoffeeEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(coffeeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        Pageable pageable = PageRequest.of(0, 5);
        Page<CoffeeDTO> result = coffeeService.findCoffees(
                null, null, null, null, null, null, null,
                null, null, null, pageable
        );

        assertTrue(result.isEmpty());
        verify(coffeeRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    /**
     * Tests that listing coffees returns a page with coffees if they exist in the DB,
     * verifying correct mapping of entities to DTOs.
     */
    @Test
    public void testListCoffeesReturnsNonEmptyPageWhenCoffeesExist() {
        Page<CoffeeEntity> coffeePage = new PageImpl<>(List.of(sampleCoffeeEntity));
        when(coffeeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(coffeePage);
        when(modelMapper.map(sampleCoffeeEntity, CoffeeDTO.class)).thenReturn(sampleCoffeeDTO);

        Pageable pageable = PageRequest.of(0, 5);
        Page<CoffeeDTO> result = coffeeService.findCoffees(
                null, null, null, null, null, null, null,
                null, null, null, pageable
        );

        assertFalse(result.isEmpty());
        assertEquals(sampleCoffeeDTO, result.getContent().get(0));
        verify(modelMapper).map(sampleCoffeeEntity, CoffeeDTO.class);
        verify(coffeeRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    /**
     * Tests that isCoffeeExists returns false if the coffee is not found.
     */
    @Test
    public void testCoffeeExistsReturnsFalseWhenCoffeeDoesntExist() {
        Long id = 1L;
        when(coffeeRepository.existsById(id)).thenReturn(false);

        boolean result = coffeeService.isCoffeeExists(id);
        assertFalse(result);
        verify(coffeeRepository).existsById(id);
    }

    /**
     * Tests that isCoffeeExists returns true if the coffee is found in the DB.
     */
    @Test
    public void testCoffeeExistsReturnsTrueWhenCoffeeDoesExist() {
        Long id = 1L;
        when(coffeeRepository.existsById(id)).thenReturn(true);

        boolean result = coffeeService.isCoffeeExists(id);
        assertTrue(result);
        verify(coffeeRepository).existsById(id);
    }

    /**
     * Tests that deleting an existing coffee results in a repository delete call.
     */
    @Test
    public void testDeleteCoffeeDeletesCoffee() {
        Long id = 1L;
        when(coffeeRepository.findById(id)).thenReturn(Optional.of(sampleCoffeeEntity));

        coffeeService.deleteCoffee(id);
        verify(coffeeRepository).delete(sampleCoffeeEntity);
    }

    /**
     * Tests that an exception (ResourceNotFoundException) is thrown
     * if the coffee to delete does not exist.
     */
    @Test
    public void testDeleteCoffeeThrowsIfCoffeeNotFound() {
        Long id = 9999L;
        when(coffeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coffeeService.deleteCoffee(id));
        verify(coffeeRepository).findById(id);
        verify(coffeeRepository, never()).deleteById(anyLong());
    }

    /**
     * Tests that updating a coffee throws ResourceNotFoundException if the coffee is missing.
     */
    @Test
    public void testUpdateCoffeeThrowsIfCoffeeNotFound() {
        Long id = 9999L;
        when(coffeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> coffeeService.updateCoffee(id, sampleCoffeeDTO));
        verify(coffeeRepository).findById(id);
        verifyNoMoreInteractions(coffeeRepository);
    }

    /**
     * Tests that updating an existing coffee calls save and returns the updated DTO.
     */
    @Test
    public void testUpdateCoffeeUpdatesSuccessfully() {
        Long id = 1L;
        when(coffeeRepository.findById(id)).thenReturn(Optional.of(sampleCoffeeEntity));
        when(coffeeRepository.save(sampleCoffeeEntity)).thenReturn(sampleCoffeeEntity);

        lenient().doNothing().when(modelMapper).map(any(CoffeeDTO.class), any(CoffeeEntity.class));
        when(modelMapper.map(any(CoffeeEntity.class), eq(CoffeeDTO.class))).thenReturn(sampleCoffeeDTO);

        Long roasteryId = sampleCoffeeDTO.getRoastery().getId();
        when(roasteryRepository.findById(eq(roasteryId)))
                .thenReturn(Optional.of(CoffeeTestData.createTestRoasteryEntity()));

        CoffeeDTO updatedCoffee = coffeeService.updateCoffee(id, sampleCoffeeDTO);
        assertEquals(sampleCoffeeDTO, updatedCoffee);
        verify(coffeeRepository).findById(id);
        verify(coffeeRepository).save(sampleCoffeeEntity);
        verify(modelMapper).map(sampleCoffeeEntity, CoffeeDTO.class);
    }
}