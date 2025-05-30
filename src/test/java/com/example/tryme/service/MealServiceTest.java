package com.example.tryme.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tryme.Model.Meal;
import com.example.tryme.Repository.MealRepository;
import com.example.tryme.exception.ResourceNotFoundException;
import com.example.tryme.services.CacheService;
import com.example.tryme.services.MealService;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private MealService mealService;

    @Test
    void bulkCreateMeals_success() {
        List<String> mealNames = Arrays.asList("Pasta", "Salad");
        Meal meal1 = mock(Meal.class);
        when(meal1.getId()).thenReturn(1L);
        when(meal1.getName()).thenReturn("Pasta");
        Meal meal2 = mock(Meal.class);
        when(meal2.getId()).thenReturn(2L);
        when(meal2.getName()).thenReturn("Salad");
        List<Meal> savedMeals = Arrays.asList(meal1, meal2);

        ArgumentCaptor<List<Meal>> captor = ArgumentCaptor.forClass(List.class);
        when(mealRepository.saveAll(captor.capture())).thenReturn(savedMeals);

        List<String> result = mealService.bulkCreateMeals(mealNames);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Meal 'Pasta' created with ID: 1", result.get(0));
        assertEquals("Meal 'Salad' created with ID: 2", result.get(1));
        verify(cacheService, times(1)).clearCache("meals");
        verify(mealRepository, times(1)).saveAll(anyList());
        assertEquals(2, captor.getValue().size());
        assertEquals("Pasta", captor.getValue().get(0).getName());
        assertEquals("Salad", captor.getValue().get(1).getName());
    }

    @Test
    void createMeal_success() {
        String mealName = "Pasta";
        Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        when(meal.getName()).thenReturn(mealName);
        when(mealRepository.save(any(Meal.class))).thenReturn(meal);

        String result = mealService.createMeal(mealName);

        assertEquals("Meal 'Pasta' created with ID: 1", result);
        verify(cacheService, times(1)).clearCache("meals");
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void getMeal_success() {
        Long id = 1L;
        Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(id);
        when(meal.getName()).thenReturn("Pasta");
        when(mealRepository.findById(id)).thenReturn(java.util.Optional.of(meal));
        when(cacheService.getFromCache("meals", "id:" + id)).thenReturn(null);

        Meal result = mealService.getMeal(id);

        assertNotNull(result);
        assertEquals("Pasta", result.getName());
        verify(cacheService, times(1)).getFromCache("meals", "id:" + id);
        verify(cacheService, times(1)).putToCache("meals", "id:" + id, List.of(meal));
        verify(mealRepository, times(1)).findById(id);
    }

    @Test
    void getMeal_notFound_throwsResourceNotFoundException() {
        Long id = 1L;
        when(mealRepository.findById(id)).thenReturn(java.util.Optional.empty());
        when(cacheService.getFromCache("meals", "id:" + id)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> mealService.getMeal(id));

        assertEquals("Meal not found with id: " + id, exception.getMessage());
        verify(cacheService, times(1)).getFromCache("meals", "id:" + id);
        verify(mealRepository, times(1)).findById(id);
    }

    @Test
    void updateMeal_success() {
        Long id = 1L;
        String newName = "New Pasta";
        Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(id);
        when(mealRepository.findById(id)).thenReturn(java.util.Optional.of(meal));
        when(mealRepository.save(any(Meal.class))).thenReturn(meal);

        String result = mealService.updateMeal(id, newName);

        assertEquals("Meal updated to 'New Pasta'", result);
        verify(cacheService, times(1)).clearCache("meals");
        verify(mealRepository, times(1)).findById(id);
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void deleteMeal_success() {
        Long id = 1L;
        Meal meal = mock(Meal.class);
        when(mealRepository.findById(id)).thenReturn(java.util.Optional.of(meal));

        String result = mealService.deleteMeal(id);

        assertEquals("Meal deleted", result);
        verify(cacheService, times(1)).clearCache("meals");
        verify(mealRepository, times(1)).findById(id);
        verify(mealRepository, times(1)).delete(meal);
    }

    @Test
    void getAllMeals_success() {
        Meal meal1 = mock(Meal.class);
        when(meal1.getName()).thenReturn("Pasta");
        Meal meal2 = mock(Meal.class);
        when(meal2.getName()).thenReturn("Salad");
        List<Meal> meals = Arrays.asList(meal1, meal2);
        when(cacheService.getFromCache("meals", "all")).thenReturn(null);
        when(mealRepository.findAll()).thenReturn(meals);

        List<Meal> result = mealService.getAllMeals();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pasta", result.get(0).getName());
        assertEquals("Salad", result.get(1).getName());
        verify(cacheService, times(1)).getFromCache("meals", "all");
        verify(cacheService, times(1)).putToCache("meals", "all", meals);
        verify(mealRepository, times(1)).findAll();
    }
}