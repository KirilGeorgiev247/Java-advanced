package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class BusinessAccount extends AccountBase {

    private Category[] allowedCategories;
    public BusinessAccount(String username, double balance, Category[] allowedCategories) {
        super(username, balance);

        if (allowedCategories == null) {
            throw new IllegalArgumentException();
        }

        this.allowedCategories = allowedCategories;
        accountType = AccountType.BUSINESS;
    }

    // TODO contains -> ok?
    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course == null) {
            courses[coursesCount++] = course;
            return;
        }
//            throw new IllegalArgumentException();

        if (balance < course.getPrice())
            throw new InsufficientBalanceException();

        if (courses.length > 100)
            throw new MaxCourseCapacityReachedException();

        if (super.getCourse(course) != null)
            throw new CourseAlreadyPurchasedException();

        for (Category category:
             allowedCategories) {
            if (category != null) {
                if (course.getCategory().equals(category)) {
                    courses[coursesCount++] = course;
                    balance -= course.getPrice() * (1 - accountType.getDiscount());
                    return;
                }
            }
        }

        throw new IllegalArgumentException();
    }
}
