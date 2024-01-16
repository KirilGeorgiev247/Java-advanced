package bg.sofia.uni.fmi.mjt.compass.api;

import bg.sofia.uni.fmi.mjt.compass.dto.RecipesResponse;
import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RecipesHttpClient {
    // TODO: prop not needed
//    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String STATUS_OK = "ok";

    private static final String STATUS_ERR = "error";
    private static final String OK_CODE = "200";
    private static final String NO_ACCESS_CODE = "403";
    private static final String BAD_REQUEST_CODE = "400";

    private static final String NOT_FOUND_CODE = "404";

    private static final Gson GSON = new Gson();

    private final HttpClient httpClient;

    public RecipesHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // TODO: some exceptions needed
    public RecipesContainer getRecipes() throws Exception {
        HttpResponse<String> response;

        try {
            // TODO: for when searching by uri
//            HttpRequest httpRequest = HttpRequest.newBuilder().uri(requestUri).setHeader().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().build();
            response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("message", e.getCause());
        }

        var parsedResponse = GSON.fromJson(response.body(), RecipesResponse.class);

        if (parsedResponse == null) {
            throw new Exception("message");
        }

        if (parsedResponse.status().equals(STATUS_ERR)) {
            if (parsedResponse.statusCode().equals(BAD_REQUEST_CODE)) {
                throw new Exception("message");
            }

            if (parsedResponse.statusCode().equals(NO_ACCESS_CODE)) {
                throw new Exception("message");
            }

            if (parsedResponse.statusCode().equals(NOT_FOUND_CODE)) {
                throw new Exception("message");
            }

            // TODO: prob else
        } else if (parsedResponse.status().equals(STATUS_OK)) {
            return new RecipesContainer(parsedResponse.recipes());
        }

        throw new Exception("Unexpected response code");
    }
}
