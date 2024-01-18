package bg.sofia.uni.fmi.mjt.compass.exception;

public class UnsuccessfulRequest extends Exception {

    private static final String CODE_WRAPPER_FROM_LEFT = " ( ";
    private static final String CODE_WRAPPER_FROM_RIGHT = " )";

    public UnsuccessfulRequest(String message) {
        super(message);
    }

    public UnsuccessfulRequest(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsuccessfulRequest(String message, int code) {
        super(message.concat(CODE_WRAPPER_FROM_LEFT.concat(String.valueOf(code).concat(CODE_WRAPPER_FROM_RIGHT))));
    }
}
