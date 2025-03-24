package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.models.RoasteryEntity;
import com.example.coffeetica.coffee.repositories.RoasteryRepository;
import com.example.coffeetica.coffee.util.CoffeeTestData;

import com.example.coffeetica.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RoasteryServiceImpl} verifying CRUD operations
 * and ResourceNotFoundException handling where applicable.
 */
@ExtendWith(MockitoExtension.class)
public class RoasteryServiceImplTest {

    @Mock
    private RoasteryRepository roasteryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoasteryServiceImpl underTest;

    private RoasteryDTO sampleRoasteryDTO;
    private RoasteryEntity sampleRoasteryEntity;

    /**
     * Initializes reusable test data before each test method.
     */
    @BeforeEach
    void setUp() {
        sampleRoasteryDTO = CoffeeTestData.createTestRoasteryDTO();
        sampleRoasteryEntity = CoffeeTestData.createTestRoasteryEntity();
    }

    /**
     * Tests that saving a new roastery properly maps DTO to entity, calls the repository's save(),
     * and maps the entity back to DTO.
     */
    @Test
    public void testThatRoasteryIsSaved() {
        when(modelMapper.map(sampleRoasteryDTO, RoasteryEntity.class)).thenReturn(sampleRoasteryEntity);
        when(roasteryRepository.save(sampleRoasteryEntity)).thenReturn(sampleRoasteryEntity);
        when(modelMapper.map(sampleRoasteryEntity, RoasteryDTO.class)).thenReturn(sampleRoasteryDTO);

        RoasteryDTO result = underTest.saveRoastery(sampleRoasteryDTO);

        assertEquals(sampleRoasteryDTO, result);
        verify(modelMapper).map(sampleRoasteryDTO, RoasteryEntity.class);
        verify(roasteryRepository).save(sampleRoasteryEntity);
        verify(modelMapper).map(sampleRoasteryEntity, RoasteryDTO.class);
    }

    /**
     * Tests that finding a roastery by its ID returns a matching DTO
     * if the roastery entity is present in the database.
     */
    @Test
    public void testThatFindByIdReturnsRoasteryWhenExists() {
        Long id = 1L;
        when(roasteryRepository.findById(id)).thenReturn(Optional.of(sampleRoasteryEntity));
        when(modelMapper.map(sampleRoasteryEntity, RoasteryDTO.class)).thenReturn(sampleRoasteryDTO);

        Optional<RoasteryDTO> result = underTest.findRoasteryById(id);

        assertTrue(result.isPresent());
        assertEquals(sampleRoasteryDTO, result.get());
        verify(roasteryRepository).findById(id);
        verify(modelMapper).map(sampleRoasteryEntity, RoasteryDTO.class);
    }

    /**
     * Tests that finding a roastery by a non-existent ID returns an empty Optional.
     */
    @Test
    public void testThatFindByIdReturnsEmptyWhenNoRoastery() {
        Long id = 999L;
        when(roasteryRepository.findById(id)).thenReturn(Optional.empty());

        Optional<RoasteryDTO> result = underTest.findRoasteryById(id);

        assertFalse(result.isPresent());
        verify(roasteryRepository).findById(id);
        verifyNoInteractions(modelMapper);
    }

    /**
     * Tests that listing roasteries returns an empty list if no roasteries exist.
     */
    @Test
    public void testListRoasteriesReturnsEmptyListWhenNoRoasteriesExist() {
        when(roasteryRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoasteryDTO> result = underTest.findAllRoasteries();

        assertTrue(result.isEmpty());
        verify(roasteryRepository).findAll();
        verifyNoInteractions(modelMapper);
    }

    /**
     * Tests that listing roasteries returns a non-empty list if roasteries exist in the database,
     * verifying correct mapping of entity to DTO for each item.
     */
    @Test
    public void testListRoasteriesReturnsRoasteriesWhenExist() {
        when(roasteryRepository.findAll()).thenReturn(List.of(sampleRoasteryEntity));
        when(modelMapper.map(sampleRoasteryEntity, RoasteryDTO.class)).thenReturn(sampleRoasteryDTO);

        List<RoasteryDTO> result = underTest.findAllRoasteries();

        assertEquals(1, result.size());
        assertEquals(sampleRoasteryDTO, result.get(0));
        verify(roasteryRepository).findAll();
        verify(modelMapper).map(sampleRoasteryEntity, RoasteryDTO.class);
    }

    /**
     * Tests that isRoasteryExists returns false if the roastery is not found in the database.
     */
    @Test
    public void testRoasteryExistsReturnsFalseWhenRoasteryDoesntExist() {
        Long id = 1L;
        when(roasteryRepository.existsById(id)).thenReturn(false);

        boolean result = underTest.isRoasteryExists(id);
        assertFalse(result);
        verify(roasteryRepository).existsById(id);
    }

    /**
     * Tests that isRoasteryExists returns true if the roastery is found in the database.
     */
    @Test
    public void testRoasteryExistsReturnsTrueWhenRoasteryDoesExist() {
        Long id = 1L;
        when(roasteryRepository.existsById(id)).thenReturn(true);

        boolean result = underTest.isRoasteryExists(id);
        assertTrue(result);
        verify(roasteryRepository).existsById(id);
    }

    /**
     * Tests that deleteRoastery deletes the roastery if it exists,
     * verifying the repository's deleteById is invoked.
     */
    @Test
    public void testDeleteRoasteryDeletesRoastery() {
        Long id = 1L;
        when(roasteryRepository.findById(id)).thenReturn(Optional.of(sampleRoasteryEntity));

        underTest.deleteRoastery(id);

        verify(roasteryRepository, times(1)).deleteById(id);
    }

    /**
     * Tests that an exception (ResourceNotFoundException) is thrown if
     * the roastery to delete does not exist.
     */
    @Test
    public void testDeleteRoasteryThrowsIfRoasteryNotFound() {
        Long id = 9999L;
        when(roasteryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> underTest.deleteRoastery(id));
        verify(roasteryRepository).findById(id);
        verify(roasteryRepository, never()).deleteById(anyLong());
    }
}