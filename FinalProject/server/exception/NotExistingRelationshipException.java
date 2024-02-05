package server.exception;

public class NotExistingRelationshipException extends Exception {
    public NotExistingRelationshipException(String message) {
        super(message);
    }

    public NotExistingRelationshipException(String message, Throwable cause) {
        super(message, cause);
    }
}
