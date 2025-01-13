package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.models.RoasteryEntity;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.util.CoffeeTestData;

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
public class RoasteryServiceImplTest {

    @Mock
    private RoasteryRepository roasteryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoasteryServiceImpl underTest;

    @Test
    public void testThatRoasteryIsSaved() {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        RoasteryEntity roasteryEntity = CoffeeTestData.createTestRoasteryEntity();

        // Mocking the behavior of ModelMapper
        when(modelMapper.map(roasteryDTO, RoasteryEntity.class)).thenReturn(roasteryEntity);
        when(roasteryRepository.save(roasteryEntity)).thenReturn(roasteryEntity);
        when(modelMapper.map(roasteryEntity, RoasteryDTO.class)).thenReturn(roasteryDTO);

        // Action
        RoasteryDTO result = underTest.saveRoastery(roasteryDTO);

        // Assertions
        assertEquals(roasteryDTO, result);
        verify(modelMapper).map(roasteryDTO, RoasteryEntity.class);
        verify(roasteryRepository).save(roasteryEntity);
        verify(modelMapper).map(roasteryEntity, RoasteryDTO.class);
    }

    @Test
    public void testThatFindByIdReturnsRoasteryWhenExists() {
        Long id = 1L;
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        RoasteryEntity roasteryEntity = CoffeeTestData.createTestRoasteryEntity();

        when(roasteryRepository.findById(id)).thenReturn(Optional.of(roasteryEntity));
        when(modelMapper.map(roasteryEntity, RoasteryDTO.class)).thenReturn(roasteryDTO);

        Optional<RoasteryDTO> result = underTest.findRoasteryById(id);

        assertTrue(result.isPresent());
        assertEquals(roasteryDTO, result.get());
        verify(roasteryRepository).findById(id);
        verify(modelMapper).map(roasteryEntity, RoasteryDTO.class);
    }

    @Test
    public void testThatFindByIdReturnEmptyWhenNoRoastery() {
        Long id = 1L;
        when(roasteryRepository.findById(id)).thenReturn(Optional.empty());

        Optional<RoasteryDTO> result = underTest.findRoasteryById(id);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testListRoasteriesReturnsEmptyListWhenNoRoasteriesExist() {
        when(roasteryRepository.findAll()).thenReturn(new ArrayList<>());

        List<RoasteryDTO> result = underTest.findAllRoasteries();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testListRoasteriesReturnsRoasteriesWhenExist() {
        RoasteryDTO roasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        RoasteryEntity roasteryEntity = CoffeeTestData.createTestRoasteryEntity();

        when(roasteryRepository.findAll()).thenReturn(List.of(roasteryEntity));
        when(modelMapper.map(roasteryEntity, RoasteryDTO.class)).thenReturn(roasteryDTO);

        List<RoasteryDTO> result = underTest.findAllRoasteries();

        assertEquals(1, result.size());
        assertEquals(roasteryDTO, result.get(0));
        verify(modelMapper).map(roasteryEntity, RoasteryDTO.class);
    }

    @Test
    public void testRoasteryExistsReturnsFalseWhenRoasteryDoesntExist() {
        Long id = 1L;
        when(roasteryRepository.existsById(id)).thenReturn(false);

        boolean result = underTest.isRoasteryExists(id);

        assertFalse(result);
    }

    @Test
    public void testRoasteryExistsReturnsTrueWhenRoasteryDoesExist() {
        Long id = 1L;
        when(roasteryRepository.existsById(id)).thenReturn(true);

        boolean result = underTest.isRoasteryExists(id);

        assertTrue(result);
    }

    @Test
    public void testDeleteRoasteryDeletesRoastery() {
        Long id = 1L;
        RoasteryEntity roasteryEntity = CoffeeTestData.createTestRoasteryEntity();

        when(roasteryRepository.findById(id)).thenReturn(Optional.of(roasteryEntity));

        underTest.deleteRoastery(id);

        verify(roasteryRepository, times(1)).deleteById(id);
    }
}
