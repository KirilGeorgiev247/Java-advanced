package bg.sofia.uni.fmi.mjt.compass.dto.response;

import bg.sofia.uni.fmi.mjt.compass.dto.response.page.NextPageWrapper;
import bg.sofia.uni.fmi.mjt.compass.dto.response.recipe.RecipeWrapper;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public record RecipesResponse(@SerializedName("_links") NextPageWrapper nextPageWrapper,
                              @SerializedName("hits") List<RecipeWrapper> recipes) {
}
