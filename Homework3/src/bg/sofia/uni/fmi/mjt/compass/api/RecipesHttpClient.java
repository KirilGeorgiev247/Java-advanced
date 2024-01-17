package bg.sofia.uni.fmi.mjt.compass.api;

import bg.sofia.uni.fmi.mjt.compass.dto.ErrorResponse;
import bg.sofia.uni.fmi.mjt.compass.dto.RecipeWrapper;
import bg.sofia.uni.fmi.mjt.compass.dto.RecipesResponse;
import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class RecipesHttpClient {
    // TODO: prop not needed - yes for now
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String STATUS_OK = "ok";
//
//    private static final String STATUS_ERR = "error";
    private static final int NO_ACCESS_CODE = 403;
    private static final int BAD_REQUEST_CODE = 400;
    private static final int NOT_FOUND_CODE = 404;
    private static final int OK_CODE = 200;
    private static final Gson GSON = new Gson();
    private final HttpClient httpClient;

    public RecipesHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // TODO: some exceptions needed
    public RecipesContainer getRecipes(RecipesRequest request) throws Exception {
        HttpResponse<String> response;

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(request.uri()).build();
            response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("message", e.getCause());
        }

        var statusCode = response.statusCode();

        if (statusCode == OK_CODE) {
            var parsedResponse = GSON.fromJson(response.body(), RecipesResponse.class);
            if (parsedResponse == null) {
                throw new Exception("message");
            }
            return new RecipesContainer(
                parsedResponse.recipes().stream().map(RecipeWrapper::recipe).filter(Objects::nonNull).toList());
        } else if (statusCode == NO_ACCESS_CODE || statusCode == BAD_REQUEST_CODE || statusCode == NOT_FOUND_CODE) {
            var parsedResponse = GSON.fromJson(response.body(), ErrorResponse.class);
            if (parsedResponse == null) {
                throw new Exception("message");
            } else {
                throw new Exception(parsedResponse.message()); // TODO: pass status code
            }
        }

        throw new Exception("Unexpected response code"); // TODO: pass status code
    }
}
