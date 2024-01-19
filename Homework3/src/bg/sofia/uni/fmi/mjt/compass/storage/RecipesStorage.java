package bg.sofia.uni.fmi.mjt.compass.storage;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesResult;

import java.util.HashMap;
import java.util.Map;

public class RecipesStorage implements Storage<String, RecipesResult> {

    Map<String, RecipesResult> storageContainer;

    Map<String, Integer> timeouts;
    private final Integer defaultTimeout;
    public RecipesStorage(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
        timeouts = new HashMap<>();
        storageContainer = new HashMap<>();
    }

    @Override
    public boolean has(String uri) {
        return storageContainer.containsKey(uri);
    }

    @Override
    public RecipesResult get(String uri) {
        Integer currTimeout = timeouts.get(uri);

        if (currTimeout == null) {
            return null;
        }

        currTimeout--;

        timeouts.put(uri, currTimeout);

        if (currTimeout <= 0) {
            storageContainer.remove(uri);
        }

        return storageContainer.getOrDefault(uri, null);
    }

    @Override
    public void put(String uri, RecipesResult recipes) {
        storageContainer.put(uri, recipes);
        timeouts.put(uri, defaultTimeout);
    }
}
