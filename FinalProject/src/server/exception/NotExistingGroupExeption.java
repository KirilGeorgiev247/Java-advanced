package server.exception;

public class NotExistingGroupExeption extends Exception {
    public NotExistingGroupExeption(String message) {
        super(message);
    }

    public NotExistingGroupExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
