package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public class EducationalAccount extends AccountBase {
    private int completedSinceDiscount = 0;

    private double getLast5Average() {
        double sum = 0.0;

        for (int i = grades.length - 5 + 1; i < grades.length; i++) {
            sum += grades[i];
        }

        return Math.round((sum / 5)*100) / 100;
    }

    public EducationalAccount(String username, double balance) {

        super(username, balance);
        accountType = AccountType.EDUCATION;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        super.buyCourse(course);

        if (completedSinceDiscount >= 5) {
            if (getLast5Average() >= 4.50) {
                balance -= course.getPrice() * (1 - accountType.getDiscount());
                completedSinceDiscount = 0;
                return;
            }
        }

        balance -= course.getPrice();
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        super.completeCourse(course, grade);
        completedSinceDiscount++;
    }
}