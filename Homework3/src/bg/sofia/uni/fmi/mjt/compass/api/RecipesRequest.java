package bg.sofia.uni.fmi.mjt.compass.api;

import bg.sofia.uni.fmi.mjt.compass.dto.recipe.type.MealType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipesRequest {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.edamam.com";
    private static final String API_ENDPOINT_PATH = "/api/recipes/v2";

    private static final String API_KEY = "1eddbfd9af2b8a2fdc21411764a67122";

    private static final String APP_ID = "d1c89079";

    private static final String API_KEY_PARAM_NAME = "api_key";

    private static final String APP_ID_PARAM_NAME = "app_id";
    private static final String KEYWORDS_PARAM_NAME = "q";
    private static final String MEAL_TYPE_PARAM = "meal-type"; // TODO: check how it is passed as a param
    private static final String HEALTH_PARAM = "health"; // TODO: check how it is passed as a param
    private static final String PAGE_PARAM = "page"; // TODO: check
    private static final String PAGE_SIZE_PARAM = "pageSize"; // TODO: check

    // TODO: -->
    private static final int ELEMENTS_ON_PAGE = 50;
    private static final int DEFAULT_PAGES_COUNT = 2;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    // TODO: <--

    private static final String DEFAULT_HEALTH_LABEL = "";

    private final List<String> keywords;
    private final MealType mealType;
    private final String healthLabel;

    private RecipesRequest(RecipesRequestBuilder builder) {
        this.keywords = builder.getKeywords();
        this.mealType = builder.getMealType();
        this.healthLabel = builder.getHealthLabel();
    }

    public static RecipesRequestBuilder newRequest(String... keywords) {
        return new RecipesRequestBuilder(keywords);
    }

    public URI uri() {
        return uri(DEFAULT_PAGE_NUMBER);
    }

    public URI uri(int page) {
        try {
            return new URI(
                API_ENDPOINT_SCHEME,
                API_ENDPOINT_HOST,
                API_ENDPOINT_PATH,
                getEndpointQuery(page),
                null);
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
        addQueryParam(sb, APP_ID_PARAM_NAME, APP_ID);
        addQueryParam(sb, API_KEY_PARAM_NAME, API_KEY);
        addQueryParam(sb, KEYWORDS_PARAM_NAME, String.join(", ", keywords)); // TODO: zapetai li sa sho e

        if (mealType != MealType.DEFAULT) {
            addQueryParam(sb, MEAL_TYPE_PARAM, mealType.toString());
        }

        if (healthLabel != null && !healthLabel.isBlank()) {
            addQueryParam(sb, HEALTH_PARAM, healthLabel);
        }

        // TODO: check logic for pagination
        addQueryParam(sb, PAGE_PARAM, String.valueOf(pageNumber));
        addQueryParam(sb, PAGE_SIZE_PARAM, String.valueOf(ELEMENTS_ON_PAGE));

        return sb.toString();
    }

    public Iterator<URI> getIterator(int elementsTotal) {
        return new PageIterator(Math.ceilDiv(elementsTotal, ELEMENTS_ON_PAGE)); // TODO: check if math.ceilDiv must be removed
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

    public static class RecipesRequestBuilder {

        private final List<String> keywords;
        private MealType mealType = MealType.DEFAULT;
        private String healthLabel = DEFAULT_HEALTH_LABEL;

        public List<String> getKeywords() {
            return keywords;
        }

        public MealType getMealType() {
            return mealType;
        }

        public String getHealthLabel() {
            return healthLabel;
        }

        private RecipesRequestBuilder(String... keywords) {
            this.keywords = new ArrayList<>();
            this.keywords.addAll(List.of(keywords));
        }

        public RecipesRequestBuilder withMealType(MealType mealType) {
            this.mealType = mealType;
            return this;
        }

        public RecipesRequestBuilder withHealthLabel(String healthLabel) {
            this.healthLabel = healthLabel;
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
}
