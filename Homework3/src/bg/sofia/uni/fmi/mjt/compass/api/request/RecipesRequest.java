package bg.sofia.uni.fmi.mjt.compass.api.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RecipesRequest implements BuiltRequest {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2";
    private static final String DEFAULT_APP_KEY = "f718de985be7df2244e8fa8445c78759";
    private static final String DEFAULT_APP_ID = "d1c89079";
    private static final String TYPE_PUBLIC = "public";
    private static final String API_KEY_PARAM_NAME = "app_key";
    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String KEYWORDS_PARAM_NAME = "q";
    private static final String TYPE_PARAM_NAME = "type";
    private static final String MEAL_TYPE_PARAM_NAME = "mealType";
    private static final String HEALTH_PARAM_NAME = "health";
    private static final String CUISINE_TYPE_PARAM_NAME = "cuisineType";
    private static final String DISH_TYPE_PARAM_NAME = "dishType";
    private final List<String> keywords;
    private final List<String> mealTypes;
    private final List<String> healthLabels;
    private final List<String> dishTypes;
    private final List<String> cuisineTypes;

    private final String appId;
    private final String appKey;

    private RecipesRequest(RecipesRequestBuilder builder) {
        keywords = builder.getKeywords();
        mealTypes = builder.getMealTypes();
        healthLabels = builder.getHealthLabels();
        dishTypes = builder.getDishTypes();
        cuisineTypes = builder.getCuisineTypes();
        appId = builder.getAppId();
        appKey = builder.getAppKey();
    }

    public static RecipesRequestBuilder newRequest() {
        return new RecipesRequestBuilder();
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
        addQueryParam(sb, KEYWORDS_PARAM_NAME, String.join(" ", keywords)); // TODO: zapetai li sa sho e
        addQueryParam(sb, APP_ID_PARAM_NAME, appId);
        addQueryParam(sb, API_KEY_PARAM_NAME, appKey);

        if (healthLabels != null && !healthLabels.isEmpty()) {
            healthLabels.forEach(hl -> addQueryParam(sb, HEALTH_PARAM_NAME, hl));
        }

        if (cuisineTypes != null && !cuisineTypes.isEmpty()) {
            cuisineTypes.forEach(ct -> addQueryParam(sb, CUISINE_TYPE_PARAM_NAME, ct));
        }

        if (mealTypes != null && !mealTypes.isEmpty()) {
            mealTypes.forEach(mt -> addQueryParam(sb, MEAL_TYPE_PARAM_NAME, mt));
        }

        if (dishTypes != null && !dishTypes.isEmpty()) {
            dishTypes.forEach(dt -> addQueryParam(sb, DISH_TYPE_PARAM_NAME, dt));
        }

        return sb.toString();
    }

    public static class RecipesRequestBuilder {
        private final List<String> keywords;
        private final List<String> mealTypes;
        private final List<String> healthLabels;
        private final List<String> dishTypes;
        private final List<String> cuisineTypes;

        private String appId;
        private String appKey;

        private RecipesRequestBuilder() {
            keywords = new ArrayList<>();
            mealTypes = new ArrayList<>();
            healthLabels = new ArrayList<>();
            dishTypes = new ArrayList<>();
            cuisineTypes = new ArrayList<>();
            appId = DEFAULT_APP_ID;
            appKey = DEFAULT_APP_KEY;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public List<String> getMealTypes() {
            return mealTypes;
        }

        public List<String> getHealthLabels() {
            return healthLabels;
        }

        public List<String> getDishTypes() {
            return dishTypes;
        }

        public List<String> getCuisineTypes() {
            return cuisineTypes;
        }

        public String getAppId() {
            return appId;
        }

        public String getAppKey() {
            return appKey;
        }

        public RecipesRequestBuilder withMealTypes(List<String> mealTypes) {
            if (mealTypes != null && !mealTypes.isEmpty()) {
                this.mealTypes.addAll(mealTypes);
            }
            return this;
        }

        public RecipesRequestBuilder withHealthLabels(List<String> healthLabels) {
            if (healthLabels != null && !healthLabels.isEmpty()) {
                this.healthLabels.addAll(healthLabels);
            }
            return this;
        }

        public RecipesRequestBuilder withCuisineTypes(List<String> cuisineTypes) {
            if (cuisineTypes != null && !cuisineTypes.isEmpty()) {
                this.cuisineTypes.addAll(cuisineTypes);
            }
            return this;
        }

        public RecipesRequestBuilder withDishTypes(List<String> dishTypes) {
            if (dishTypes != null && !dishTypes.isEmpty()) {
                this.dishTypes.addAll(dishTypes);
            }
            return this;
        }

        public RecipesRequestBuilder withKeywords(List<String> keywords) {
            if (keywords != null && !keywords.isEmpty()) {
                this.keywords.addAll(keywords);
            }
            return this;
        }

        public RecipesRequestBuilder withAppId(String appId) {
            if (appId != null && !appId.isBlank()) {
                this.appId = appId;
            }
            return this;
        }

        public RecipesRequestBuilder withAppKey(String appKey) {
            if (appKey != null && !appKey.isBlank()) {
                this.appKey = appKey;
            }
            return this;
        }

        public RecipesRequest build() {
            return new RecipesRequest(this);
        }
    }
}
