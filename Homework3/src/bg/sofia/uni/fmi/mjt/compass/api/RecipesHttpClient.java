package bg.sofia.uni.fmi.mjt.compass.api;

import bg.sofia.uni.fmi.mjt.compass.dto.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.compass.dto.response.RecipesResponse;
import bg.sofia.uni.fmi.mjt.compass.dto.response.page.NextPageWrapper;
import bg.sofia.uni.fmi.mjt.compass.dto.response.recipe.RecipeWrapper;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulParsing;
import bg.sofia.uni.fmi.mjt.compass.exception.UnsuccessfulRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class RecipesHttpClient {
    private static final int NO_ACCESS_CODE = 403;
    private static final int BAD_REQUEST_CODE = 400;
    private static final int NOT_FOUND_CODE = 404;
    private static final int OK_CODE = 200;
    private static final String EMPTY_STRING = "";
    private static final Gson GSON = new Gson();
    private final HttpClient httpClient;

    public RecipesHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public RecipesResult executeRecipesRequest(URI uri) throws UnsuccessfulRequest {
        HttpResponse<String> response;

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).build();
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new UnsuccessfulRequest("Request failed", e.getCause());
        }

        return getParsedResult(response);
    }

    String parseNextPageUri(NextPageWrapper nextPageWrapper) {
        if (nextPageWrapper == null || nextPageWrapper.next() == null || nextPageWrapper.next().href() == null) {
            return EMPTY_STRING;
        }

        return nextPageWrapper.next().href();
    }

    RecipesResult getParsedResult(HttpResponse<String> response) throws UnsuccessfulRequest {
        var statusCode = response.statusCode();

        if (statusCode == OK_CODE) {
            var parsedResponse = GSON.fromJson(response.body(), RecipesResponse.class);
            if (parsedResponse == null) {
                throw new UnsuccessfulParsing("Unsuccessful recipes result parsing");
            }
            return new RecipesResult(parseNextPageUri(parsedResponse.nextPageWrapper()),
                parsedResponse.recipes().stream().map(RecipeWrapper::recipe).filter(Objects::nonNull).toList());
        } else if (statusCode == NO_ACCESS_CODE || statusCode == BAD_REQUEST_CODE || statusCode == NOT_FOUND_CODE) {
            var parsedResponse = GSON.fromJson(response.body(), ErrorResponse.class);
            if (parsedResponse == null) {
                throw new UnsuccessfulParsing("Unsuccessful error result parsing");
            } else {
                throw new UnsuccessfulRequest(parsedResponse.message(), statusCode);
            }
        }

        throw new UnsuccessfulRequest("Unexpected response code", statusCode);
    }
}
