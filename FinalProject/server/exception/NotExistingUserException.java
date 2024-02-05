package server.exception;

public class NotExistingUserException extends Exception {

    public NotExistingUserException(String message) {
        super(message);
    }

    public NotExistingUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
