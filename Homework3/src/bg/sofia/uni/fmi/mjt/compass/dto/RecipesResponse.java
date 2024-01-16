package bg.sofia.uni.fmi.mjt.compass.dto;

import bg.sofia.uni.fmi.mjt.compass.dto.recipe.Recipe;

import java.util.List;

public record RecipesResponse(String status, String statusCode, String message, int resultsCount,
                              List<Recipe> recipes) {
}
