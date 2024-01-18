package bg.sofia.uni.fmi.mjt.compass.storage;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesResult;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RecipesStorage implements Storage<URI, RecipesResult> {

    Map<URI, RecipesResult> storageContainer;

    Map<URI, Integer> timeouts;
    private final Integer defaultTimeout;
    public RecipesStorage(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
        timeouts = new HashMap<>();
        storageContainer = new HashMap<>();
    }

    @Override
    public boolean has(URI uri) {
        return storageContainer.containsKey(uri);
    }

    @Override
    public RecipesResult get(URI uri) {
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
    public void put(URI uri, RecipesResult recipes) {
        storageContainer.put(uri, recipes);
        timeouts.put(uri, defaultTimeout);
    }
}
