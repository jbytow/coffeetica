package com.example.coffeetica.services.impl;
import com.example.coffeetica.model.Coffee;
import com.example.coffeetica.repositories.CoffeeRepository;

import com.example.coffeetica.util.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoffeeServiceImplTest {

    @Mock
    private CoffeeRepository coffeeRepository;

    @InjectMocks
    private CoffeeServiceImpl underTest;

    @Test
    public void testThatCoffeeIsSaved() {
        final Coffee coffee = TestData.createTestCoffee();

        when(coffeeRepository.save(eq(coffee))).thenReturn(coffee);

        final Coffee result = underTest.saveCoffee(coffee);
        assertEquals(coffee, result);
    }

    @Test
    public void testThatFindByIdReturnEmptyWhenNoCoffee() {
        final Long id = 1L;
        when(coffeeRepository.findById(eq(id))).thenReturn(Optional.empty());

        final Optional<Coffee> result = underTest.findCoffeeById(id);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testThatFindByIdReturnsCoffeeWhenExists() {
        final Coffee coffee = TestData.createTestCoffee();

        when(coffeeRepository.findById(eq(coffee.getId()))).thenReturn(Optional.of(coffee));

        final Optional<Coffee> result = underTest.findCoffeeById(coffee.getId());
        assertEquals(Optional.of(coffee), result);
    }

    @Test
    public void testListCoffeesReturnsEmptyListWhenNoCoffeesExist() {
        when(coffeeRepository.findAll()).thenReturn(new ArrayList<>());
        final List<Coffee> result = underTest.findAllCoffees();
        assertEquals(0, result.size());
    }

    @Test
    public void testListCoffeesReturnsCoffeesWhenExist() {
        final Coffee coffee = TestData.createTestCoffee();
        when(coffeeRepository.findAll()).thenReturn(List.of(coffee));
        final List<Coffee> result = underTest.findAllCoffees();
        assertEquals(1, result.size());
    }

    @Test
    public void testCoffeeExistsReturnsFalseWhenCoffeeDoesntExist() {
        when(coffeeRepository.existsById(any())).thenReturn(false);
        final boolean result = underTest.isCoffeeExists(1L);
        assertEquals(false, result);
    }

    @Test
    public void testCoffeeExistsReturnsFalseWhenCoffeeDoesExist() {
        when(coffeeRepository.existsById(any())).thenReturn(true);
        final boolean result = underTest.isCoffeeExists(1L);
        assertEquals(true, result);
    }

    @Test
    public void testDeleteCoffeeDeletesCoffee() {
        final Long id = 1L;
        underTest.deleteCoffee(id);
        verify(coffeeRepository, times(1)).deleteById(eq(id));
    }
}
