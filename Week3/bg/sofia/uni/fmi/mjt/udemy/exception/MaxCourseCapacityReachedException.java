package bg.sofia.uni.fmi.mjt.udemy.exception;

public class MaxCourseCapacityReachedException extends Exception{
    public MaxCourseCapacityReachedException() {
        super("Max course capacity is reached");
    }
}
