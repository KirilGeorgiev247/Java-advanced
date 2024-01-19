package bg.sofia.uni.fmi.mjt.compass.api;

import bg.sofia.uni.fmi.mjt.compass.dto.Recipe;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulParsing;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipesHttpClientTest {

    private static final String INVALID_RES_JSON = "invalid test";
    private static final String ERROR_RES_JSON = """
            {
            "status": "error",
            "message": "Unauthorized app_id = d1c89079"
        }""";

    private static final String RECIPES_RES_WITH_NEXT = """
        {
            "from": 1,
            "to": 20,
            "count": 10000,
            "_links": {
                "next": {
                    "href": "https://api.edamam.com/api/recipes/v2?q=&app_key=f718de985be7df2244e8fa8445c78759&_cont=CHcVQBtNNQphDmgVQntAEX4BY0t3AwAVX3dCVWIaZVMlDFJSFzYSV2MVNVdwB1FVEWVACzQWNwNzBxFqX3cWQT1OcV9xBE4%3D&cuisineType=american&type=public&app_id=d1c89079",
                    "title": "Next page"
                }
            },
            "hits": [
                {
                    "recipe": {
                        "uri": "http://www.edamam.com/ontologies/edamam.owl#recipe_4bb99424e1bbc40d3cd1d891883d6745",
                        "label": "Frothy Iced Matcha Green Tea Recipe",
                        "dietLabels": ["High-Protein"],
                        "healthLabels": ["Sugar-Conscious"],
                        "totalWeight": 232.796185,
                        "cuisineType": ["american"],
                        "mealType": ["lunch/dinner"],
                        "dishType": ["drinks"],
                        "ingredientLines": [
                            "2 teaspoons (6g) Japanese matcha green tea",
                            "8 ounces (235ml) cold water"
                        ]
                    }
                }
            ]
        }
        """;

    private static final String RECIPES_RES_WITHOUT_NEXT = """
        {
            "hits": [
                {
                    "recipe": {
                        "uri": "http://www.edamam.com/ontologies/edamam.owl#recipe_4bb99424e1bbc40d3cd1d891883d6745",
                        "label": "Frothy Iced Matcha Green Tea Recipe",
                        "dietLabels": ["High-Protein"],
                        "healthLabels": ["Sugar-Conscious"],
                        "totalWeight": 232.796185,
                        "cuisineType": ["american"],
                        "mealType": ["lunch/dinner"],
                        "dishType": ["drinks"],
                        "ingredientLines": [
                            "2 teaspoons (6g) Japanese matcha green tea",
                            "8 ounces (235ml) cold water"
                        ]
                    }
                }
            ]
        }
        """;
    private static final String TEST = "http://test.com";

    private static final int SERVER_ERROR_CODE = 500;
    private static final int STATUS_OK = 200;
    private static final int NO_ACCESS_CODE = 403;

    private static final URI TEST_URI = URI.create(TEST);

    @Mock
    HttpClient httpClient;

    @Mock
    HttpResponse<String> response;

    RecipesHttpClient recipesHttpClient;

    @Test
    void testIfUnsuccessfulRequestIsThrownWhenHttpClientThrowsIOException() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenThrow(IOException.class);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulRequest.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Recipes http client should throw when http client throws!");
    }

    @Test
    void testIfUnsuccessfulRequestIsThrownWhenHttpClientThrowsIllegalArgumentException()
        throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenThrow(
            IllegalArgumentException.class);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulRequest.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Recipes http client should throw when http client throws!");
    }

    @Test
    void testIfUnsuccessfulRequestIsThrownWhenHttpClientThrowsInterruptedException()
        throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenThrow(InterruptedException.class);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulRequest.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Recipes http client should throw when http client throws!");
    }

    @Test
    void testIfUnexpectedStatusCodeThrows() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(SERVER_ERROR_CODE);

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(response);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulRequest.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Recipes http client should thrown when unexpected status code");
    }

    @Test
    void testIfUnsuccessfulParsingIsThrownWhenInvalidJSONIsReturnedAsResponse()
        throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(STATUS_OK);
        when(response.body()).thenReturn(INVALID_RES_JSON);

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(response);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulParsing.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Unsuccessful parsing should be thrown when response has invalid JSON!");
    }

    @Test
    void testIfErrorResponseIsParsedCorrectlyAndThrowsRight() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(NO_ACCESS_CODE);
        when(response.body()).thenReturn(ERROR_RES_JSON);

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(response);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulRequest.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Unsuccessful request exception should be thrown when error response is parsed correctly!");
    }

    @Test
    void testIfParsingThrowsWhenParsedResponseIsNull() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(STATUS_OK);
        when(response.body()).thenReturn(null);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()))
            .thenReturn(response);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulParsing.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Recipes http client should throw unsuccessful parsing when the parsed response is null!");
    }

    @Test
    void testIfParsingThrowsWhenErrorParsedResponseIsNull() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(NO_ACCESS_CODE);
        when(response.body()).thenReturn(null);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()))
            .thenReturn(response);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        assertThrows(UnsuccessfulParsing.class, () -> recipesHttpClient.executeRecipesRequest(TEST_URI),
            "Recipes http client should throw unsuccessful parsing when the error parsed response is null!");
    }

    @Test
    void testIfRecipesResponseIsParsedCorrectly() throws IOException, InterruptedException, UnsuccessfulRequest {
        when(response.statusCode()).thenReturn(STATUS_OK);
        when(response.body()).thenReturn(RECIPES_RES_WITH_NEXT);

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(response);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        var result = recipesHttpClient.executeRecipesRequest(TEST_URI);

        boolean isNextNullOrEmpty = result.nextPageUri() == null || result.nextPageUri().isBlank();
        int recipesCount = result.recipes().size();

        boolean isAnyRecipePropNullOrEmpty = checkRecipeProps(result.recipes().getFirst());

        boolean hasParsingFailed = isNextNullOrEmpty || recipesCount != 1 || isAnyRecipePropNullOrEmpty;

        assertFalse(hasParsingFailed, "Recipes response should return filled object when there are so!");
    }

    @Test
    void testIfRecipesResponseWithoutNextIsParsedCorrectly()
        throws IOException, InterruptedException, UnsuccessfulRequest {
        when(response.statusCode()).thenReturn(STATUS_OK);
        when(response.body()).thenReturn(RECIPES_RES_WITHOUT_NEXT);

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(TEST_URI).build();
        when(httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(response);

        recipesHttpClient = new RecipesHttpClient(httpClient);

        var result = recipesHttpClient.executeRecipesRequest(TEST_URI);

        boolean isNextNullOrEmpty = result.nextPageUri() == null || result.nextPageUri().isBlank();
        int recipesCount = result.recipes().size();

        boolean isAnyRecipePropNullOrEmpty = checkRecipeProps(result.recipes().getFirst());

        boolean hasParsingFailed = !isNextNullOrEmpty || recipesCount != 1 || isAnyRecipePropNullOrEmpty;

        assertFalse(hasParsingFailed, "Recipes response should return filled object when there are so and next should be null!");
    }

    private boolean checkRecipeProps(Recipe recipe) {
        return recipe.uri() == null || recipe.uri().isBlank() ||
            recipe.label() == null || recipe.label().isBlank() ||
            recipe.dietLabels() == null || recipe.dietLabels().isEmpty() ||
            recipe.healthLabels() == null || recipe.healthLabels().isEmpty() ||
            recipe.cuisineType() == null || recipe.cuisineType().isEmpty() ||
            recipe.mealType() == null || recipe.mealType().isEmpty() ||
            recipe.dishType() == null || recipe.dishType().isEmpty() ||
            recipe.ingredientLines() == null || recipe.ingredientLines().isEmpty() ||
            recipe.totalWeight() == Float.parseFloat(BigDecimal.ZERO.toString());
    }
}
