package bg.sofia.uni.fmi.mjt.compass.exception;

public class UncheckedURISyntaxException extends RuntimeException {
    public UncheckedURISyntaxException(String message) {
        super(message);
    }

    public UncheckedURISyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
