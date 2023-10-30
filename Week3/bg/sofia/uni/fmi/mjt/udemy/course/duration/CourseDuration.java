package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration(int hours, int minutes) {

    public CourseDuration {
        if (hours < 0 || hours > 24 || minutes < 0 || minutes > 60)
            throw new IllegalArgumentException();
    }

    public static CourseDuration of(Resource[] content) {
        int minutes = 0;

        for (int i = 0; i < content.length; i++) {
            if (content[i] != null) {
                minutes += content[i].getDuration().minutes();
            }
        }

        int hours = minutes / 60;
        minutes = minutes % 60;

        return new CourseDuration(hours, minutes);
    }

}
