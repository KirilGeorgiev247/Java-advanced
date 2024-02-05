package server.response;

import java.util.List;

public record Response(int statusCode, List<String> info) {
    
}
