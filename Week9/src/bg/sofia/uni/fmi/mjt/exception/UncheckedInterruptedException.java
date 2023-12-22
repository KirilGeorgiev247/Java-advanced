package bg.sofia.uni.fmi.mjt.exception;

public class UncheckedInterruptedException extends RuntimeException {
    public UncheckedInterruptedException(String message) {
        super(message);
    }

    public UncheckedInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
