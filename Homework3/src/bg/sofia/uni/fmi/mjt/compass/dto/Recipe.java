package bg.sofia.uni.fmi.mjt.compass.dto;

import java.util.Arrays;
import java.util.List;

public record Recipe(String uri, String label, List<String> dietLabels, List<String> healthLabels,
                     float totalWeight, List<String> cuisineType, List<String> mealType, List<String> dishType,
                     List<String> ingredientLines) {
    private static final String ID_DELIMITER = "#";
    public String getId() {
        return Arrays.stream(uri().split(ID_DELIMITER)).toList().getLast();
    }
}
