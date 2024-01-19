package bg.sofia.uni.fmi.mjt.compass.api.request;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecipesRequestTest {

    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2";
    private static final String DEFAULT_APP_KEY = "f718de985be7df2244e8fa8445c78759";
    private static final String DEFAULT_APP_ID = "d1c89079";
    private static final String TYPE_PUBLIC = "public";
    private static final String APP_KEY_PARAM_NAME = "app_key";
    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String TYPE_PARAM_NAME = "type";
    private static final String KEYWORDS_PARAM_NAME = "q";
    private static final String MEAL_TYPE_PARAM_NAME = "mealType";
    private static final String HEALTH_PARAM_NAME = "health";
    private static final String CUISINE_TYPE_PARAM_NAME = "cuisineType";
    private static final String DISH_TYPE_PARAM_NAME = "dishType";

    private static final String TEST_TEXT = "test";
    private static final String TEST_APP_KEY = "test app key";
    private static final String TEST_APP_ID = "test app id";

    private static final List<String> KEYWORDS = List.of(TEST_TEXT, TEST_TEXT);
    private static final List<String> MEAL_TYPES = List.of(TEST_TEXT);
    private static final List<String> HEALTH_LABELS = List.of(TEST_TEXT, TEST_TEXT, TEST_TEXT);
    private static final List<String> CUISINE_TYPES = List.of(TEST_TEXT);
    private static final List<String> DISH_TYPES = List.of(TEST_TEXT);
    private BuiltRequest request;

    @Test
    void testIfRequestIsBuiltCorrectlyWhenValidData() throws URISyntaxException {
        StringBuilder resultQuery = new StringBuilder();
        addQueryParam(resultQuery, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(resultQuery, KEYWORDS_PARAM_NAME, String.join(" ", KEYWORDS));
        addQueryParam(resultQuery, APP_ID_PARAM_NAME, TEST_APP_ID);
        addQueryParam(resultQuery, APP_KEY_PARAM_NAME, TEST_APP_KEY);

        HEALTH_LABELS.forEach(hl -> addQueryParam(resultQuery, HEALTH_PARAM_NAME, hl));
        CUISINE_TYPES.forEach(ct -> addQueryParam(resultQuery, CUISINE_TYPE_PARAM_NAME, ct));
        MEAL_TYPES.forEach(mt -> addQueryParam(resultQuery, MEAL_TYPE_PARAM_NAME, mt));
        DISH_TYPES.forEach(dt -> addQueryParam(resultQuery, DISH_TYPE_PARAM_NAME, dt));

        URI expectedResult =
            new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, resultQuery.toString(),
                null);

        request = RecipesRequest.newRequest()
            .withAppKey(TEST_APP_KEY)
            .withAppId(TEST_APP_ID)
            .withKeywords(KEYWORDS)
            .withCuisineTypes(CUISINE_TYPES)
            .withHealthLabels(HEALTH_LABELS)
            .withDishTypes(DISH_TYPES)
            .withMealTypes(MEAL_TYPES)
            .build();

        assertEquals(expectedResult, request.uri(),
            "Uri should have the same parameters as added!");
    }

    @Test
    void testIfRequestIsBuildCorrectlyWhenNoOrInvalidData() throws URISyntaxException {
        StringBuilder resultQuery = new StringBuilder();
        addQueryParam(resultQuery, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(resultQuery, KEYWORDS_PARAM_NAME, String.join(" ", KEYWORDS));
        addQueryParam(resultQuery, APP_ID_PARAM_NAME, DEFAULT_APP_ID);
        addQueryParam(resultQuery, APP_KEY_PARAM_NAME, DEFAULT_APP_KEY);

        URI expectedResult =
            new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, resultQuery.toString(),
                null);

        request = RecipesRequest.newRequest()
            .withKeywords(KEYWORDS)
            .build();

        assertEquals(expectedResult, request.uri(),
            "Uri should have the same parameters as added!");
    }

    private void addQueryParam(StringBuilder builder, String queryParamName, String queryParamValue) {
        builder.append(queryParamName);
        builder.append("=");
        builder.append(queryParamValue);
        builder.append("&");
    }
}
