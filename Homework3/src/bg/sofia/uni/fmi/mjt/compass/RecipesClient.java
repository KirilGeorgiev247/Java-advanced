package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesContainer;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesHttpClient;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesRequest;
import bg.sofia.uni.fmi.mjt.compass.storage.RecipesStorage;

public class RecipesClient {
    private static final int DEFAULT_TIMEOUT = 10;
    private final RecipesHttpClient client;
    private final RecipesStorage recipesStorage;

    public RecipesClient(RecipesHttpClient client) {
        this(client, DEFAULT_TIMEOUT);
    }

    public RecipesClient(RecipesHttpClient client, int defaultTimeout) {
        this.client = client;
        recipesStorage = new RecipesStorage(defaultTimeout);
    }

    public RecipesContainer execute(RecipesRequest request) throws Exception {
        if (recipesStorage.has(request)) {
            return recipesStorage.get(request);
        }

        RecipesContainer recipes = client.getRecipes();

        recipesStorage.put(request, recipes);

        return recipes;
    }
}
