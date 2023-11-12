package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Member implements GymMember, Comparable<Member> {

    private final Address address;
    private final String name;
    private final int age;

    private final String personalIdNumber;
    private final Gender gender;

    private final Map<DayOfWeek, Workout> trainingProgram;

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {
        this.address = address;
        this.name = name;
        this.age = age;
        this.personalIdNumber = personalIdNumber;
        this.gender = gender;
        this.trainingProgram = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getPersonalIdNumber() {
        return personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return Map.copyOf(trainingProgram);
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {
        if (day == null || workout == null) {
            throw new IllegalArgumentException();
        }

        trainingProgram.put(day, workout);
    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty() || exerciseName.isBlank()) {
            throw new IllegalArgumentException();
        }

        Collection<DayOfWeek> daysFinishingWithEx = new HashSet<>();

        for (Map.Entry<DayOfWeek, Workout> element : trainingProgram.entrySet()) {
            if (element.getValue().exercises().getLast().name().equals(exerciseName)) {
                daysFinishingWithEx.add(element.getKey());
            }
        }

        return daysFinishingWithEx;
    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) {
        if (day == null || exercise == null || exercise.name() == null ||
            exercise.name().isEmpty() || exercise.name().isBlank()) {
            throw new IllegalArgumentException();
        }

        Workout workout = trainingProgram.get(day);

        if (workout == null) {
            throw new DayOffException();
        }

        workout.exercises().addLast(exercise);

        trainingProgram.put(day, workout);
    }

    @Override
    public void addExercises(DayOfWeek day, List<Exercise> exercises) {
        if (day == null || exercises == null || exercises.isEmpty()) {
            throw new IllegalArgumentException();
        }

        for (Exercise ex : exercises) {
            if (ex == null || ex.name() == null || ex.name().isBlank() || ex.name().isEmpty()) {
                throw new IllegalArgumentException();
            }
        }

        for (Exercise ex : exercises) {
            if (ex != null) {
                Workout workout = trainingProgram.get(day);

                if (workout == null) {
                    throw new DayOffException();
                }

                workout.exercises().addLast(ex);
                trainingProgram.put(day, workout);
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o == null) return false;

        if (!(o instanceof Member m)) return false;

        return this.personalIdNumber.equals(m.getPersonalIdNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalIdNumber);
    }

    @Override
    public int compareTo(Member m) {
        return String.CASE_INSENSITIVE_ORDER.compare(personalIdNumber, m.getPersonalIdNumber());
    }
}
