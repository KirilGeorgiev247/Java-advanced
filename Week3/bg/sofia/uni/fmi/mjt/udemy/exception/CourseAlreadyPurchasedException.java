package bg.sofia.uni.fmi.mjt.udemy.exception;

public class CourseAlreadyPurchasedException extends Exception{
    public CourseAlreadyPurchasedException() {
        super("Course is already purchased");
    }
}
