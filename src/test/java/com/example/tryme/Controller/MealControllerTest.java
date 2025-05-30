package com.example.tryme.Controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.tryme.exception.BadRequestException;
import com.example.tryme.services.MealService;

@ExtendWith(MockitoExtension.class)
class MealControllerTest {

    @Mock
    private MealService mealService;

    @InjectMocks
    private MealController mealController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void bulkCreateMeals_success() {
        List<String> mealNames = Arrays.asList("Pasta", "Salad");
        List<String> expectedResponse = Arrays.asList("Meal 'Pasta' created with ID: 1", "Meal 'Salad' created with ID: 2");
        when(mealService.bulkCreateMeals(mealNames)).thenReturn(expectedResponse);

        ResponseEntity<List<String>> response = mealController.bulkCreateMeals(mealNames);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(mealService, times(1)).bulkCreateMeals(mealNames);
    }

    @Test
    void bulkCreateMeals_emptyList_throwsBadRequestException() {
        List<String> emptyList = Arrays.asList();

        BadRequestException exception = assertThrows(BadRequestException.class, () -> mealController.bulkCreateMeals(emptyList));

        assertEquals("Список 'mealNames' не может быть пустым.", exception.getMessage());
        verify(mealService, times(0)).bulkCreateMeals(anyList());
    }

    @Test
    void bulkCreateMeals_nullList_throwsBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> mealController.bulkCreateMeals(null));

        assertEquals("Список 'mealNames' не может быть пустым.", exception.getMessage());
        verify(mealService, times(0)).bulkCreateMeals(anyList());
    }

    @Test
    void bulkCreateMeals_nullOrEmptyNames_throwsBadRequestException() {
        List<String> invalidList = Arrays.asList("Pasta", "", null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> mealController.bulkCreateMeals(invalidList));

        assertEquals("Названия блюд не могут быть пустыми.", exception.getMessage());
        verify(mealService, times(0)).bulkCreateMeals(anyList());
    }

    @Test
    void bulkCreateMeals_duplicateNames_success() {
        List<String> mealNames = Arrays.asList("Pasta", "Pasta", "Salad");
        List<String> expectedResponse = Arrays.asList(
                "Meal 'Pasta' created with ID: 1",
                "Meal 'Pasta' created with ID: 2",
                "Meal 'Salad' created with ID: 3"
        );
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        when(mealService.bulkCreateMeals(captor.capture())).thenReturn(expectedResponse);

        ResponseEntity<List<String>> response = mealController.bulkCreateMeals(mealNames);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(mealNames, captor.getValue());
        verify(mealService, times(1)).bulkCreateMeals(mealNames);
    }
}