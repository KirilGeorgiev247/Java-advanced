package bg.sofia.uni.fmi.mjt.udemy.exception;

public class CourseNotPurchasedException extends Exception{
    public CourseNotPurchasedException() {
        super("Course is not purchased");
    }
}
