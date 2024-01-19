package bg.sofia.uni.fmi.mjt.compass.api.request;

import java.net.URI;
import java.net.URISyntaxException;

public class RecipeByUriRequest implements BuiltRequest {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2/by-uri";
    private static final String DEFAULT_APP_KEY = "f718de985be7df2244e8fa8445c78759";
    private static final String DEFAULT_APP_ID = "d1c89079";
    private static final String TYPE_PUBLIC = "public";
    private static final String API_KEY_PARAM_NAME = "app_key";
    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String TYPE_PARAM_NAME = "type";
    private static final String URI_PARAM_NAME = "uri";
    private static final String DEFAULT_URI_VALUE = "";
    private final String uri;
    private final String appId;
    private final String appKey;

    private RecipeByUriRequest(RecipeByUriRequestBuilder builder) {
        uri = builder.getUri();
        appId = builder.getAppId();
        appKey = builder.getAppKey();
    }

    public static RecipeByUriRequestBuilder newRequest() {
        return new RecipeByUriRequestBuilder();
    }

    @Override
    public URI uri() throws URISyntaxException {
        return new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, getEndpointQuery(), null);
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
        addQueryParam(sb, URI_PARAM_NAME, uri);
        addQueryParam(sb, APP_ID_PARAM_NAME, appId);
        addQueryParam(sb, API_KEY_PARAM_NAME, appKey);

        return sb.toString();
    }

    public static class RecipeByUriRequestBuilder {
        private String uri;
        private String appId;
        private String appKey;

        private RecipeByUriRequestBuilder() {
            appId = DEFAULT_APP_ID;
            appKey = DEFAULT_APP_KEY;
            uri = DEFAULT_URI_VALUE;
        }

        public String getUri() {
            return uri;
        }

        public String getAppId() {
            return appId;
        }

        public String getAppKey() {
            return appKey;
        }

        public RecipeByUriRequestBuilder withUri(String uri) {
            if (uri != null) {
                this.uri = uri;
            }
            return this;
        }

        public RecipeByUriRequestBuilder withUri(URI uri) {
            if (uri != null) {
                this.uri = uri.toString();
            }
            return this;
        }

        public RecipeByUriRequestBuilder withAppId(String appId) {
            if (appId != null && !appId.isBlank()) {
                this.appId = appId;
            }
            return this;
        }

        public RecipeByUriRequestBuilder withAppKey(String appKey) {
            if (appKey != null && !appKey.isBlank()) {
                this.appKey = appKey;
            }
            return this;
        }

        public RecipeByUriRequest build() {
            return new RecipeByUriRequest(this);
        }
    }
}
