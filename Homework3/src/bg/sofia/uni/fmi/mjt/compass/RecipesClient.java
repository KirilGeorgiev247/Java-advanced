package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesResult;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesHttpClient;
import bg.sofia.uni.fmi.mjt.compass.api.request.BuiltRequest;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulRequest;
import bg.sofia.uni.fmi.mjt.compass.iterator.PageIterator;
import bg.sofia.uni.fmi.mjt.compass.storage.RecipesStorage;

import java.net.URI;
import java.util.Iterator;

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

    public RecipesResult execute(BuiltRequest request, int pagesCount) throws UnsuccessfulRequest {
        if (recipesStorage.has(request.uri())) {
            return recipesStorage.get(request.uri());
        }

        RecipesResult result = client.executeRecipesRequest(request.uri());
        recipesStorage.put(request.uri(), result);

        Iterator<RecipesResult> iterator = new PageIterator(client, result);
        int iterations = Math.max(2, pagesCount);

        for (int i = 0; i < iterations - 1; i++) {
            if (iterator.hasNext()) {
                RecipesResult currResult = iterator.next();
                recipesStorage.put(URI.create(result.nextPageUri()), currResult);
                result.concat(currResult.recipes());
                result.changeUri(currResult.nextPageUri());
            } else {
                break;
            }
        }

        return result;
    }

    public RecipesResult execute(BuiltRequest request) throws UnsuccessfulRequest {
        return this.execute(request, 1);
    }
}
