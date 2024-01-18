package bg.sofia.uni.fmi.mjt.compass.exception;

public class UnsuccessfulParsing extends RuntimeException {
    public UnsuccessfulParsing(String message) {
        super(message);
    }

    public UnsuccessfulParsing(String message, Throwable cause) {
        super(message, cause);
    }
}
