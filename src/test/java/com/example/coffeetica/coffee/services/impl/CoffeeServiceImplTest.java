package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.model.CoffeeDTO;
import com.example.coffeetica.coffee.model.CoffeeEntity;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.services.impl.CoffeeServiceImpl;
import com.example.coffeetica.coffee.util.TestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        CoffeeEntity coffeeEntity = TestData.createTestCoffeeEntity();

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
        CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        CoffeeEntity coffeeEntity = TestData.createTestCoffeeEntity();

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
        when(coffeeRepository.findAll()).thenReturn(new ArrayList<>());

        List<CoffeeDTO> result = underTest.findAllCoffees();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testListCoffeesReturnsCoffeesWhenExist() {
        CoffeeDTO coffeeDTO = TestData.createTestCoffeeDTO();
        CoffeeEntity coffeeEntity = TestData.createTestCoffeeEntity();

        when(coffeeRepository.findAll()).thenReturn(List.of(coffeeEntity));
        when(modelMapper.map(coffeeEntity, CoffeeDTO.class)).thenReturn(coffeeDTO);

        List<CoffeeDTO> result = underTest.findAllCoffees();

        assertEquals(1, result.size());
        assertEquals(coffeeDTO, result.get(0));
        verify(modelMapper).map(coffeeEntity, CoffeeDTO.class);
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
    public void testDeleteCoffeeDeletesCoffee() {
        Long id = 1L;

        underTest.deleteCoffee(id);

        verify(coffeeRepository, times(1)).deleteById(id);
    }
}
