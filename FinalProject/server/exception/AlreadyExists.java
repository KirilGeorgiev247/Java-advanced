package server.exception;

public class AlreadyExists extends Exception {
    public AlreadyExists(String message) {
        super(message);
    }

    public AlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}
