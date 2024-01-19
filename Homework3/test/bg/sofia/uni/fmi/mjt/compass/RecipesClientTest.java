package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesHttpClient;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesResult;
import bg.sofia.uni.fmi.mjt.compass.api.request.RecipesRequest;
import bg.sofia.uni.fmi.mjt.compass.dto.Recipe;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipesClientTest {
    private static final String TEST_URI = "http://test.com";

    @Mock
    private RecipesHttpClient httpClient;

    @Mock
    private RecipesRequest recipesRequest;

    @Mock
    private Recipe recipe;

    @Test
    void testIfRecipesRequestReturnsCorrectPagesCount() throws UnsuccessfulRequest, URISyntaxException {
        int defaultPages = 2;
        URI uri = URI.create(TEST_URI);

        List<Recipe> recipes = List.of(recipe);
        int recipesPerResultCount = recipes.size();

        RecipesResult result = new RecipesResult(TEST_URI, recipes);

        when(httpClient.executeRecipesRequest(uri)).thenReturn(result);
        when(recipesRequest.uri()).thenReturn(uri);

        RecipesClient client = new RecipesClient(httpClient);
        var defaultResult = client.execute(recipesRequest);

        assertEquals(defaultPages * recipesPerResultCount, defaultResult.recipes().size(),
            "Pages should be two by default!");
    }

    @Test
    void testIfRecipesRequestReturnsCorrectlyWhenCustomCount() throws UnsuccessfulRequest, URISyntaxException {
        int sevenPages = 7;
        URI uri = URI.create(TEST_URI);

        List<Recipe> recipes = List.of(recipe);
        int recipesPerResultCount = recipes.size();

        RecipesResult result = new RecipesResult(TEST_URI, recipes);

        when(httpClient.executeRecipesRequest(uri)).thenReturn(result);
        when(recipesRequest.uri()).thenReturn(uri);

        RecipesClient client = new RecipesClient(httpClient);
        var defaultResult = client.execute(recipesRequest, sevenPages);

        assertEquals(Math.pow(2, sevenPages - 1) * recipesPerResultCount, defaultResult.recipes().size(),
            "Pages should be as much as told!");
    }
}
