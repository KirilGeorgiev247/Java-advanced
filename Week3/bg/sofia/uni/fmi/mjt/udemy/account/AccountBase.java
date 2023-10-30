package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;

public class AccountBase implements Account {

    protected AccountType accountType;
    protected String username;
    protected double balance;

    protected Course[] courses = new Course[100];

    protected int coursesCount = 0;

    protected double[] grades = new double[100];

    protected int gradesCount = 0;

    protected Course getCourse(Course toFind) {
        for (Course c :
                courses) {
            if (c != null) {
                if (c.equals(toFind))
                    return c;
            }
        }

        return null;
    }

    public AccountBase(String username, double balance) {
//        if (username == null || username.isBlank() || username.isEmpty() || balance < 0.0)
//            throw new IllegalArgumentException();

        this.username = username;
        this.balance = balance;
    }

    @Override
    public String getUsername() {

        return username;
    }

    @Override
    public void addToBalance(double amount) {
        if (amount < 0.0)
            throw new IllegalArgumentException();

        balance += amount;
    }

    @Override
    public double getBalance() {

        return balance;
    }

    // TODO business
    // TODO check is course is purchased - ок??
    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course == null) {
            courses[coursesCount++] = course;
            return;
        }

        if (balance < course.getPrice())
            throw new InsufficientBalanceException();

        if (coursesCount >= 100)
            throw new MaxCourseCapacityReachedException();

        if (getCourse(course) != null)
            throw new CourseAlreadyPurchasedException();

        courses[coursesCount++] = course;
    }

    // TODO when equals is implemented ... -> ok?
    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        if (course == null || resourcesToComplete == null)
            throw new IllegalArgumentException();

        Course c = getCourse(course);

        if (c == null)
            throw new CourseNotPurchasedException();

        Resource currRes = null;

        for (Resource r:
             resourcesToComplete) {
            if (c.hasResource(r)) {
                currRes = r;
                break;
            }
        }

        if (currRes == null)
            throw new ResourceNotFoundException();

        currRes.complete();
    }

    // TODO -> ?
    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if (grade < 2.00 || grade > 6.00)
            throw new IllegalArgumentException();

        Course c = getCourse(course);

        if (c == null)
            throw new CourseNotPurchasedException();

        for (Resource r :
                c.getContent()) {
            if (r == null || !r.isCompleted())
                throw new CourseNotCompletedException();
        }

        if (gradesCount < 100)
            grades[gradesCount++] = Math.round(grade * 100) / 100;

        // TODO complete course logic?
    }

    @Override
    public Course getLeastCompletedCourse() {
        if (courses.length == 0)
            return null;

        Course result = null;

        for (Course c:
             courses) {
            if (c != null) {
                result = c;
                break;
            }
        }

        if (result == null) {
            return null;
        }

        for (Course c :
                courses) {
            if (c != null) {
                if (c.getCompletionPercentage() < result.getCompletionPercentage())
                    result = c;
            }
        }

        return result;
    }
}
