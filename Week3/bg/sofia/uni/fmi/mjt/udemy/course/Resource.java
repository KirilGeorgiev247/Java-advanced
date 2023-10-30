package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

public class Resource implements Completable {

    private String name;
    private ResourceDuration duration;

    private boolean isCompleted = false;
    public Resource(String name, ResourceDuration duration) {
//        if (name == null || name.isBlank() || name.isEmpty() || duration == null) {
//            throw new IllegalArgumentException();
//        }

        this.name = name;
        this.duration = duration;
    }


    /**
     * Returns the resource name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total duration of the resource.
     */
    public ResourceDuration getDuration() {
        return duration;
    }

    /**
     * Marks the resource as completed.
     */
    public void complete() {
        isCompleted = true;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    // TODO - ok??
    @Override
    public int getCompletionPercentage() {
        if (isCompleted)
            return 100;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (o == null)
            return false;

        if (!(o instanceof Resource))
            return false;

        Resource r = (Resource) o;

        return this.name.equals(r.getName()) &&
               this.isCompleted == r.isCompleted() &&
               this.duration.equals(r.getDuration());
    }
}
