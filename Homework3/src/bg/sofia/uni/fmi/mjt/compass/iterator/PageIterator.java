package bg.sofia.uni.fmi.mjt.compass.iterator;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesHttpClient;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesResult;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulRequest;

import java.net.URI;
import java.util.Iterator;

public class PageIterator implements Iterator<RecipesResult> {

    private final RecipesHttpClient httpClient;
    private RecipesResult curr;

    public PageIterator(RecipesHttpClient httpClient, RecipesResult begin) {

        this.httpClient = httpClient;
        curr = begin;
    }

    @Override
    public boolean hasNext() {
        return curr.nextPageUri() != null && !curr.nextPageUri().isBlank();
    }

    @Override
    public RecipesResult next() {
        if (!hasNext()) {
            throw new IndexOutOfBoundsException("No next exists");
        }

        return getNext();
    }

    private RecipesResult getNext() {
        try {
            URI nextUri = URI.create(curr.nextPageUri());
            curr = httpClient.executeRecipesRequest(nextUri);
            return curr;
        } catch (UnsuccessfulRequest e) {
            return null;
        }
    }
}
