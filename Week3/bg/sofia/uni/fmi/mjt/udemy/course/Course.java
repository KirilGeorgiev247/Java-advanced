package bg.sofia.uni.fmi.mjt.udemy.course;
import java.util.Arrays;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.lang.reflect.Array;

public class Course implements Completable, Purchasable{

    private String name;
    private String description;
    private double price;
    private Resource[] content;

    private Category category;

    private boolean isPurchased = false;

    // TODO validation - ok?
    public Course(String name, String description, double price, Resource[] content, Category category) {
//        if (name == null || name.isEmpty() || name.isBlank())
//            throw new IllegalArgumentException();
//
//        if (price < 0.0 || content == null || category == null || description == null)
//            throw new IllegalArgumentException();

        this.name = name;
        this.description = description;
        this.price = price;
        this.content = content;
        this.category = category;
    }

    /**
     * Returns the name of the course.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the course.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the price of the course.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the category of the course.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the content of the course.
     */
    public Resource[] getContent() {
        return content;
    }

    /**
     * Returns the total duration of the course.
     */
    public CourseDuration getTotalTime() {

        return CourseDuration.of(content);
    }

    /**
     * Completes a resource from the course.
     *
     * @param resourceToComplete the resource which will be completed.
     * @throws IllegalArgumentException if resourceToComplete is null.
     * @throws ResourceNotFoundException if the resource could not be found in the course.
     */

    // TODO equals resource - ok?
    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if (resourceToComplete == null)
            throw new IllegalArgumentException();

        boolean isFound = false;

        Resource curr = null;

        for (Resource r :
                content) {
            if (r.equals(resourceToComplete)) {
                curr = r;
                break;
            }
        }

        if (curr == null)
            throw new ResourceNotFoundException();

        curr.complete();

        // TODO: add implementation here - ok?
    }

    @Override
    public boolean isCompleted() {
        for (Resource r :
                content) {
            if (r != null) {
                if (!r.isCompleted())
                    return false;
            }
        }

        return true;
    }

    // TODO if empty? - ok?
    @Override
    public int getCompletionPercentage() {
        if (content.length == 0)
            return 0;

        double completed = 0.0;

        for (Resource r :
                content) {
            if (r.isCompleted())
                completed++;
        }

        double result = Math.round((completed / content.length) * 100 );

        return (int)result;
    }

    @Override
    public void purchase() {
        isPurchased = true;
    }

    @Override
    public boolean isPurchased() {

        return isPurchased;
    }

    public boolean hasResource(Resource resource) {
        for (Resource r:
             content) {
            if (r != null) {
                if (resource.equals(r))
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (o == null)
            return false;

        if (!(o instanceof Course))
            return false;

        Course c = (Course) o;

        return this.name.equals(c.getName()) &&
                this.description.equals(c.getDescription()) &&
                Double.compare(this.price, c.getPrice()) == 0 &&
                this.category.equals(c.getCategory()) &&
                this.isPurchased == c.isPurchased() &&
                Arrays.deepEquals(this.content, c.getContent());
    }
}
