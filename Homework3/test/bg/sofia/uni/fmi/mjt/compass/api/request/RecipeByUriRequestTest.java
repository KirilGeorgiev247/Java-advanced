package bg.sofia.uni.fmi.mjt.compass.api.request;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecipeByUriRequestTest {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2/by-uri";
    private static final String DEFAULT_APP_KEY = "f718de985be7df2244e8fa8445c78759";
    private static final String DEFAULT_APP_ID = "d1c89079";
    private static final String TYPE_PUBLIC = "public";
    private static final String APP_KEY_PARAM_NAME = "app_key";
    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String TYPE_PARAM_NAME = "type";
    private static final String URI_PARAM_NAME = "uri";
    private static final String DEFAULT_URI_VALUE = "";

    private static final String TEST_APP_KEY = "test app key";
    private static final String TEST_APP_ID = "test app id";
    private static final String TEST_URI_VALUE = "test uri value";
    private static final String TEST_URI = "http://test.com";

    private BuiltRequest request;

    @Test
    void testIfRequestIsBuiltCorrectlyWhenValidData() throws URISyntaxException {
        StringBuilder resultQuery = new StringBuilder();
        addQueryParam(resultQuery, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(resultQuery, URI_PARAM_NAME, TEST_URI_VALUE);
        addQueryParam(resultQuery, APP_ID_PARAM_NAME, TEST_APP_ID);
        addQueryParam(resultQuery, APP_KEY_PARAM_NAME, TEST_APP_KEY);

        URI expectedResult =
            new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, resultQuery.toString(),
                null);

        request = RecipeByUriRequest.newRequest()
            .withAppKey(TEST_APP_KEY)
            .withAppId(TEST_APP_ID)
            .withUri(TEST_URI_VALUE)
            .build();

        assertEquals(expectedResult, request.uri(),
            "Uri should have the same parameters as added!");
    }

    @Test
    void testIfRequestIsBuiltCorrectlyWithUriObject() throws URISyntaxException {
        StringBuilder resultQuery = new StringBuilder();
        addQueryParam(resultQuery, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(resultQuery, URI_PARAM_NAME, TEST_URI);
        addQueryParam(resultQuery, APP_ID_PARAM_NAME, TEST_APP_ID);
        addQueryParam(resultQuery, APP_KEY_PARAM_NAME, TEST_APP_KEY);

        URI expectedResult =
            new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, resultQuery.toString(),
                null);

        URI uri = URI.create(TEST_URI);

        request = RecipeByUriRequest.newRequest()
            .withAppKey(TEST_APP_KEY)
            .withAppId(TEST_APP_ID)
            .withUri(uri)
            .build();

        assertEquals(expectedResult, request.uri(),
            "Uri should have the same parameters as added even when passed uri as param is object!");
    }

    @Test
    void testIfRequestIsBuildCorrectlyWhenNoOrInvalidData() throws URISyntaxException {
        StringBuilder resultQuery = new StringBuilder();
        addQueryParam(resultQuery, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(resultQuery, URI_PARAM_NAME, DEFAULT_URI_VALUE);
        addQueryParam(resultQuery, APP_ID_PARAM_NAME, DEFAULT_APP_ID);
        addQueryParam(resultQuery, APP_KEY_PARAM_NAME, DEFAULT_APP_KEY);

        URI expectedResult =
            new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, resultQuery.toString(),
                null);

        request = RecipeByUriRequest.newRequest()
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
