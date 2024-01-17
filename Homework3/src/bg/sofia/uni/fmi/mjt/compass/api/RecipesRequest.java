package bg.sofia.uni.fmi.mjt.compass.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipesRequest {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2";
    private static final String API_KEY = "f718de985be7df2244e8fa8445c78759";
    private static final String APP_ID = "d1c89079";
    private static final String TYPE_PUBLIC = "public";
    private static final String API_KEY_PARAM_NAME = "app_key";
    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String KEYWORDS_PARAM_NAME = "q";
    private static final String TYPE_PARAM_NAME = "type";
    private static final String MEAL_TYPE_PARAM_NAME = "mealType";
    private static final String HEALTH_PARAM_NAME = "health";
    private static final String CUISINE_TYPE_PARAM_NAME = "cuisineType";
    private static final String DISH_TYPE_PARAM_NAME = "dishType";

    // TODO: -->
    private static final String PAGE_PARAM = "page"; // TODO: check
    private static final String PAGE_SIZE_PARAM = "pageSize"; // TODO: check
    private static final int ELEMENTS_ON_PAGE = 50;
    private static final int DEFAULT_PAGES_COUNT = 2;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    // TODO: <--
    private final List<String> keywords;
    private final List<String> mealTypes;
    private final List<String> healthLabels;
    private final List<String> dishTypes;
    private final List<String> cuisineTypes;

    private RecipesRequest(RecipesRequestBuilder builder) {
        this.keywords = builder.getKeywords();
        this.mealTypes = builder.getMealTypes();
        this.healthLabels = builder.getHealthLabels();
        this.dishTypes = builder.getDishTypes();
        this.cuisineTypes = builder.getCuisineTypes();
    }

    public static RecipesRequestBuilder newRequest() {
        return new RecipesRequestBuilder();
    }

    public URI uri() {
        return uri(DEFAULT_PAGE_NUMBER);
    }

    public URI uri(int page) {
        try {
            return new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, getEndpointQuery(page), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void addQueryParam(StringBuilder builder, String queryParamName, String queryParamValue) {
        builder.append(queryParamName);
        builder.append("=");
        builder.append(queryParamValue);
        builder.append("&");
    }

    private String getEndpointQuery(int pageNumber) {
        StringBuilder sb = new StringBuilder();
        addQueryParam(sb, TYPE_PARAM_NAME, TYPE_PUBLIC);
        addQueryParam(sb, KEYWORDS_PARAM_NAME, String.join(", ", keywords)); // TODO: zapetai li sa sho e
        addQueryParam(sb, APP_ID_PARAM_NAME, APP_ID);
        addQueryParam(sb, API_KEY_PARAM_NAME, API_KEY);

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

        // TODO: check logic for pagination
//        addQueryParam(sb, PAGE_PARAM, String.valueOf(pageNumber));
//        addQueryParam(sb, PAGE_SIZE_PARAM, String.valueOf(ELEMENTS_ON_PAGE));

        return sb.toString();
    }

    public Iterator<URI> getIterator(int elementsTotal) {
        return new PageIterator(
            Math.ceilDiv(elementsTotal, ELEMENTS_ON_PAGE)); // TODO: check if math.ceilDiv must be removed
    }

    public static class RecipesRequestBuilder {
        private final List<String> keywords;
        private final List<String> mealTypes;
        private final List<String> healthLabels;
        private final List<String> dishTypes;
        private final List<String> cuisineTypes;

        private RecipesRequestBuilder() {
            this.keywords = new ArrayList<>();
            this.mealTypes = new ArrayList<>();
            this.healthLabels = new ArrayList<>();
            this.dishTypes = new ArrayList<>();
            this.cuisineTypes = new ArrayList<>();
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

        public RecipesRequestBuilder withMealTypes(String... mealTypes) {
            this.mealTypes.addAll(List.of(mealTypes));
            return this;
        }

        public RecipesRequestBuilder withHealthLabels(String... healthLabels) {
            this.healthLabels.addAll(List.of(healthLabels));
            return this;
        }

        public RecipesRequestBuilder withCuisineType(String... cuisineTypes) {
            this.cuisineTypes.addAll(List.of(cuisineTypes));
            return this;
        }

        public RecipesRequestBuilder withDishTypes(String... dishTypes) {
            this.dishTypes.addAll(List.of(dishTypes));
            return this;
        }

        public RecipesRequestBuilder withKeywords(String... keywords) {
            this.keywords.addAll(List.of(keywords));
            return this;
        }

        // TODO: add logic for these ones so they are not hardcoded
//        public RecipesRequestBuilder withAppId(String appId) {}
//        public RecipesRequestBuilder withApiKey(String apiKey) {}

        public RecipesRequest build() {
            return new RecipesRequest(this);
        }
    }

    private class PageIterator implements Iterator<URI> {

        private final int pages;
        private int currentPage = 2; // TODO: why 2

        private PageIterator(int pages) {
            this.pages = pages;
        }

        @Override
        public boolean hasNext() {
            return currentPage <= pages;
        }

        @Override
        public URI next() {
            return uri(currentPage++);
        }
    }
}
