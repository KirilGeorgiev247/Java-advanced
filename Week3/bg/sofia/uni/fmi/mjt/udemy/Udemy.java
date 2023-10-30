package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;

public class Udemy implements LearningPlatform {

    private Account[] accounts;
    private Course[] courses;

    private boolean isKeywordValid(char[] keyword) {
        for (int i = 0; i < keyword.length; i++) {
            if (!(keyword[i] >= 'a' && keyword[i] <= 'z' || keyword[i] >= 'A' && keyword[i] <= 'Z'))
                return false;
        }

        return true;
    }

    public Udemy(Account[] accounts, Course[] courses) {
//        if (accounts == null || courses == null) {
//            throw new IllegalArgumentException();
//        }

        // check for null
        this.accounts = accounts;

        // check for null
        this.courses = courses;
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {
        if (name == null || name.isBlank() || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (courses != null) {
            for (Course c: courses) {
                if (c != null){
                    if (c.getName().equals(name)) return c;
                }
            }
        }

        throw new CourseNotFoundException();
    }

    @Override
    public Course[] findByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank() || keyword.isEmpty())
            throw new IllegalArgumentException();

        if (!isKeywordValid(keyword.toCharArray()))
            throw new IllegalArgumentException();

        int count = 0;

        for (Course c :
                courses) {
            if (c != null) {
                if (c.getName().contains(keyword) || c.getDescription().contains(keyword))
                    count++;
            }
        }

        Course[] result = new Course[count];

        int index = 0;

        for (Course c:
             courses) {
            if (c != null) {
                if (c.getName().contains(keyword) || c.getDescription().contains(keyword))
                    result[index++] = c;
            }
        }

        return result;
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
        if (category == null)
            throw new IllegalArgumentException();

        int count = 0;

        for (Course c :
                courses) {
            if (c != null) {
                if (c.getCategory().equals(category))
                    count++;
            }
        }

        Course[] result = new Course[count];

        int index = 0;

        for (Course c:
                courses) {
            if (c != null) {
                if (c.getCategory().equals(category))
                    result[index++] = c;
            }
        }

        return result;
    }

    // check if username is mean by name
    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        if (name == null || name.isBlank() || name.isEmpty())
            throw new IllegalArgumentException();

        if (accounts != null) {
            for (Account acc :
                    accounts) {
                if (acc != null) {
                    if (acc.getUsername().equals(name)) {
                        return acc;
                    }
                }
            }
        }

        throw new AccountNotFoundException();
    }

    @Override
    public Course getLongestCourse() {
        if (courses == null || courses.length == 0)
            return null;

        Course result = null;

        for (Course c:
             courses) {
            if (c != null && c.getTotalTime() != null) {
                result = c;
                break;
            }
        }

        if (result == null) {
            return null;
        }

        for (Course c :
                courses) {
            if (c != null && c.getTotalTime() != null && result.getTotalTime() != null) {
                if (c.getTotalTime().hours() > result.getTotalTime().hours())
                    result = c;
                else if (c.getTotalTime().hours() == result.getTotalTime().hours() &&
                        c.getTotalTime().minutes() > result.getTotalTime().minutes())
                    result = c;
            }
        }

        return result;
    }

    @Override
    public Course getCheapestByCategory(Category category) {
        if (category == null)
            throw new IllegalArgumentException();

        if (courses.length == 0)
            return null;

        Course result = null;

        for (Course c :
                courses) {
            if (c != null) {
                if (c.getCategory().equals(category)) {
                    result = c;
                    break;
                }
            }
        }

        if (result == null) {
            return null;
        }

        for (Course c :
                courses) {
            if (c != null) {
                if (c.getCategory().equals(category) &&
                        c.getPrice() < result.getPrice())
                    result = c;
            }
        }

        return result;
    }
}
