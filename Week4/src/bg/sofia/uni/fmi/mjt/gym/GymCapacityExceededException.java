package bg.sofia.uni.fmi.mjt.gym;

public class GymCapacityExceededException extends Exception {

    public GymCapacityExceededException() {
        super("Gym max capacity is reached!");
    }
}
