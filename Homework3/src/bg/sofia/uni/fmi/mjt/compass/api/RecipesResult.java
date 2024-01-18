package bg.sofia.uni.fmi.mjt.compass.api;

import bg.sofia.uni.fmi.mjt.compass.dto.Recipe;

import java.util.LinkedList;
import java.util.List;

public final class RecipesResult {
    private String nextPageUri;
    private final List<Recipe> recipes = new LinkedList<>();

    public RecipesResult(String nextPageUri, List<Recipe> recipes) {
        this.nextPageUri = nextPageUri;
        this.recipes.addAll(recipes);
    }

    public String nextPageUri() {
        return nextPageUri;
    }

    public List<Recipe> recipes() {
        return recipes;
    }

    public void concat(List<Recipe> recipes) {
        this.recipes.addAll(recipes);
    }

    public void changeUri(String nextPageUri) {
        this.nextPageUri = nextPageUri;
    }
}
