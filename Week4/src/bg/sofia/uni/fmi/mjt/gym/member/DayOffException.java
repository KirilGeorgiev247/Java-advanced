package bg.sofia.uni.fmi.mjt.gym.member;

public class DayOffException extends RuntimeException {
    public DayOffException() {
        super("Selected day is a day off!");
    }
}
