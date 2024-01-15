package bg.sofia.uni.fmi.mjt.compass.dto;

import bg.sofia.uni.fmi.mjt.compass.dto.type.CuisineType;
import bg.sofia.uni.fmi.mjt.compass.dto.type.DishType;
import bg.sofia.uni.fmi.mjt.compass.dto.type.MealType;

import java.util.List;

public record Recipe(String URI, String label, List<String> dietLabels, List<String> healthLabels, float totalWeight,
                     CuisineType cuisineType, MealType mealType, DishType dishType, List<String> ingredientLines) {
}
