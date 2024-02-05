package server.exception;

public class NotAuthenticated extends Exception {
    public NotAuthenticated(String message) {
        super(message);
    }

    public NotAuthenticated(String message, Throwable cause) {
        super(message, cause);
    }
}
