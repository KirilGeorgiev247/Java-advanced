import bg.sofia.uni.fmi.mjt.compass.RecipesClient;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesHttpClient;
import bg.sofia.uni.fmi.mjt.compass.api.RecipesRequest;

import java.net.http.HttpClient;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws Exception {
        final int timeout = 10;
        RecipesHttpClient httpClient = new RecipesHttpClient(HttpClient.newHttpClient());
        RecipesClient client = new RecipesClient(httpClient, timeout);
        var result = client.execute(RecipesRequest.newRequest().withKeywords("chicken").build());
    }
}
