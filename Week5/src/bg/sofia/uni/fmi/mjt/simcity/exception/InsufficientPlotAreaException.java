package bg.sofia.uni.fmi.mjt.simcity.exception;

public class InsufficientPlotAreaException extends RuntimeException {

    public InsufficientPlotAreaException() {
        super("There is not enough plot area!");
    }
}
