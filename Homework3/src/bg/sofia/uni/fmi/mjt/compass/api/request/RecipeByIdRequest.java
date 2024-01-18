package bg.sofia.uni.fmi.mjt.compass.api.request;

import bg.sofia.uni.fmi.mjt.compass.exception.UncheckedURISyntaxException;

import java.net.URI;
import java.net.URISyntaxException;

public class RecipeByIdRequest implements BuiltRequest {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2/";
    private static final String DEFAULT_APP_KEY = "f718de985be7df2244e8fa8445c78759";
    private static final String DEFAULT_APP_ID = "d1c89079";
    private static final String TYPE_PUBLIC = "public";
    private static final String API_KEY_PARAM_NAME = "app_key";
    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String TYPE_PARAM_NAME = "type";

    private static final String DEFAULT_ID_VALUE = "";
    private final String id;
    private final String appId;
    private final String appKey;

    private RecipeByIdRequest(RecipeByIdRequestBuilder builder) {
        id = builder.getId();
        appId = builder.getAppId();
        appKey = builder.getAppKey();
    }

    public static RecipeByIdRequestBuilder newRequest() {
        return new RecipeByIdRequestBuilder();
    }

    @Override
    public URI uri() {
        try {
            return new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH.concat(id), getEndpointQuery(),
                null);
        } catch (URISyntaxException e) {
            throw new UncheckedURISyntaxException("There was a syntax error in uri creation", e.getCause());
        }
    }

    private void addQueryParam(StringBuilder builder, String queryParamName, String queryParamValue) {
        builder.append(queryParamName);
        builder.append("=");
        builder.append(queryParamValue);
        builder.append("&");
    }

    private String getEndpointQuery() {
        StringBuilder sb = new StringBuilder();
        addQueryParam(sb, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(sb, APP_ID_PARAM_NAME, appId);
        addQueryParam(sb, API_KEY_PARAM_NAME, appKey);

        return sb.toString();
    }

    public static class RecipeByIdRequestBuilder {
        private String id;

        private String appId;
        private String appKey;

        private RecipeByIdRequestBuilder() {
            appId = DEFAULT_APP_ID;
            appKey = DEFAULT_APP_KEY;
            id = DEFAULT_ID_VALUE;
        }

        public String getId() {
            return id;
        }

        public String getAppId() {
            return appId;
        }

        public String getAppKey() {
            return appKey;
        }

        public RecipeByIdRequestBuilder withId(String id) {
            if (id != null) {
                this.id = id;
            }
            return this;
        }

        public RecipeByIdRequestBuilder withAppId(String appId) {
            if (appId != null && !appId.isBlank()) {
                this.appId = appId;
            }
            return this;
        }

        public RecipeByIdRequestBuilder withAppKey(String appKey) {
            if (appKey != null && !appKey.isBlank()) {
                this.appKey = appKey;
            }
            return this;
        }

        public RecipeByIdRequest build() {
            return new RecipeByIdRequest(this);
        }
    }
}
