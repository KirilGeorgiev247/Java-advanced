package bg.sofia.uni.fmi.mjt.compass.storage;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesContainer;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesRequest;
import bg.sofia.uni.fmi.mjt.compass.dto.recipe.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipesStorage implements Storage<RecipesRequest, RecipesContainer> {

    Map<RecipesRequest, RecipesContainer> storageContainer;

    Map<RecipesRequest, Integer> timeouts;
    private final Integer defaultTimeout;
    public RecipesStorage(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
        timeouts = new HashMap<>();
        storageContainer = new HashMap<>();
    }

    @Override
    public boolean has(RecipesRequest request) {
        return storageContainer.containsKey(request);
    }

    @Override
    public RecipesContainer get(RecipesRequest request) {
        Integer currTimeout = timeouts.get(request);

        if (currTimeout == null) {
            return null; // TODO: optional
        }

        currTimeout--;

        timeouts.put(request, currTimeout);

        if (currTimeout <= 0) {
            storageContainer.remove(request);
        }

        return storageContainer.getOrDefault(request, null);
    }

    @Override
    public void put(RecipesRequest request, RecipesContainer recipes) {
        storageContainer.put(request, recipes);
        timeouts.put(request, defaultTimeout);
    }
}
