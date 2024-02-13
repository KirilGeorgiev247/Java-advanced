package server.response;

import server.response.status.ServerStatusCode;

public record Response(int statusCode, String info) {

    public static Response decline(ServerStatusCode statusCode, String errorMessage) {
        return new Response(statusCode.getCode(), errorMessage);
    }

    public static Response ok(ServerStatusCode statusCode, String info) {
        return new Response(statusCode.getCode(), info);
    }
}
