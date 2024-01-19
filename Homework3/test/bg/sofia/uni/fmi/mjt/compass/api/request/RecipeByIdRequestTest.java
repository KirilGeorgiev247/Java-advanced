package bg.sofia.uni.fmi.mjt.compass.api.request;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecipeByIdRequestTest {

    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2/";
    private static final String DEFAULT_APP_KEY = "f718de985be7df2244e8fa8445c78759";
    private static final String DEFAULT_APP_ID = "d1c89079";
    private static final String TYPE_PUBLIC = "public";
    private static final String APP_KEY_PARAM_NAME = "app_key";
    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String TYPE_PARAM_NAME = "type";
    private static final String DEFAULT_ID_VALUE = "";

    private static final String TEST_APP_KEY = "test app key";
    private static final String TEST_APP_ID = "test app id";
    private static final String TEST_ID = "test id";

    private BuiltRequest request;

    @Test
    void testIfRequestIsBuiltCorrectlyWhenValidData() throws URISyntaxException {
        StringBuilder resultQuery = new StringBuilder();
        addQueryParam(resultQuery, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(resultQuery, APP_ID_PARAM_NAME, TEST_APP_ID);
        addQueryParam(resultQuery, APP_KEY_PARAM_NAME, TEST_APP_KEY);

        URI expectedResult =
            new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH.concat(TEST_ID), resultQuery.toString(),
                null);

        request = RecipeByIdRequest.newRequest()
            .withAppKey(TEST_APP_KEY)
            .withAppId(TEST_APP_ID)
            .withId(TEST_ID)
            .build();

        assertEquals(expectedResult, request.uri(),
            "Uri should have the same parameters as added!");
    }

    @Test
    void testIfRequestIsBuildCorrectlyWhenNoOrInvalidData() throws URISyntaxException {
        StringBuilder resultQuery = new StringBuilder();
        addQueryParam(resultQuery, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(resultQuery, APP_ID_PARAM_NAME, DEFAULT_APP_ID);
        addQueryParam(resultQuery, APP_KEY_PARAM_NAME, DEFAULT_APP_KEY);

        URI expectedResult =
            new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH.concat(DEFAULT_ID_VALUE), resultQuery.toString(),
                null);

        request = RecipeByIdRequest.newRequest()
            .withAppKey("")
            .withAppId(null)
            .build();

        assertEquals(expectedResult, request.uri(),
            "Uri should match with default params when invalid or no are passed as added!");
    }
    private void addQueryParam(StringBuilder builder, String queryParamName, String queryParamValue) {
        builder.append(queryParamName);
        builder.append("=");
        builder.append(queryParamValue);
        builder.append("&");
    }
}
