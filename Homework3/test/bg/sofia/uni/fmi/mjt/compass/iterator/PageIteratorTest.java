package bg.sofia.uni.fmi.mjt.compass.iterator;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesHttpClient;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesResult;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PageIteratorTest {

    private static final String EMPTY_STRING = "";
    private static final String TEST = "test";

    @Mock
    RecipesResult recipesResult;

    @Mock
    RecipesHttpClient recipesHttpClient;

    @Mock
    RecipesResult finalRecipesResult;

    Iterator<RecipesResult> iterator;

    @Test
    void testIfHasNextReturnsCorrectly() {
        when(recipesResult.nextPageUri()).thenReturn(TEST);

        iterator = new PageIterator(recipesHttpClient, recipesResult);

        assertTrue(iterator.hasNext(),
            "Iterator should return that there is next element when next uri is not null or empty string!");

        when(recipesResult.nextPageUri()).thenReturn(EMPTY_STRING);

        iterator = new PageIterator(recipesHttpClient, recipesResult);

        assertFalse(iterator.hasNext(), "Iterator should return false when next uri is empty string!");

        when(recipesResult.nextPageUri()).thenReturn(null);

        assertFalse(iterator.hasNext(), "Iterator should return false when next uri is null!");
    }

    @Test
    void testIfNextThrowsWhenHasNextIsFalse() {
        when(recipesResult.nextPageUri()).thenReturn(null);

        iterator = new PageIterator(recipesHttpClient, recipesResult);

        assertThrows(IndexOutOfBoundsException.class, () -> iterator.next(),
            "Iterator should throw if next is invoked when has next is false!");
    }

    @Test
    void testIfNextIsReturningCorrectResult() throws UnsuccessfulRequest {
        URI uri = URI.create(TEST);

        when(recipesResult.nextPageUri()).thenReturn(TEST);

        when(recipesHttpClient.executeRecipesRequest(uri)).thenReturn(finalRecipesResult);

        iterator = new PageIterator(recipesHttpClient, recipesResult);

        assertEquals(finalRecipesResult, iterator.next(),
            "Next should return the response from the http client and the prev recipes result next uri!");
    }

    @Test
    void testIfNextReturnsNullWhenUnsuccessfulRequest() throws UnsuccessfulRequest {
        URI uri = URI.create(TEST);

        when(recipesResult.nextPageUri()).thenReturn(TEST);

        when(recipesHttpClient.executeRecipesRequest(uri)).thenThrow(UnsuccessfulRequest.class);

        iterator = new PageIterator(recipesHttpClient, recipesResult);

        assertNull(iterator.next(),
            "Next should return null when http client throws unsuccessful request exception!");
    }
}
