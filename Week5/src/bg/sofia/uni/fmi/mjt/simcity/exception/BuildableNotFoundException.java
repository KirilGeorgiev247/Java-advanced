package bg.sofia.uni.fmi.mjt.simcity.exception;

public class BuildableNotFoundException extends RuntimeException {

    public BuildableNotFoundException() {
        super("Building not found!");
    }
}
