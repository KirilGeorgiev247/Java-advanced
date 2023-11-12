package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.MemberByNameComparator;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Gym implements GymAPI {

    private final Address address;
    private SortedSet<GymMember> members;
    private int capacity = 0;
    private int count;

    public Gym(int capacity, Address address) {
        this.capacity = capacity;
        this.address = address;
        this.members = new TreeSet<>();
        this.count = 0;
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return Collections.unmodifiableSortedSet(members);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        SortedSet<GymMember> membersSortedByName = new TreeSet<>(new MemberByNameComparator());
        membersSortedByName.addAll(members);
        return Collections.unmodifiableSortedSet(membersSortedByName);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        SortedSet<GymMember> membersSortedByProximity = new TreeSet<>(new MemberByProximityComparator());
        membersSortedByProximity.addAll(members);
        return Collections.unmodifiableSortedSet(membersSortedByProximity);
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {
        if (member == null) {
            throw new IllegalArgumentException();
        }

        if (count + 1 >= capacity) {
            throw new GymCapacityExceededException();
        }

        members.add(member);
        count++;
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (count + members.size() >= capacity) {
            throw new GymCapacityExceededException();
        }

        for (GymMember member :
            members) {
            if (member != null && member.getName() != null &&
                !member.getName().isBlank() && member.getAddress() != null &&
                member.getPersonalIdNumber() != null && !member.getPersonalIdNumber().isBlank() &&
                member.getAge() > 0 && member.getTrainingProgram() != null &&
                member.getGender() != null) {
                this.members.add(member);
                count++;
            }
        }
    }

    @Override
    public boolean isMember(GymMember member) {
        if (member == null) {
            throw new IllegalArgumentException();
        }

        return members.contains(member);
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {
        if (day == null || exerciseName == null || exerciseName.isBlank() || exerciseName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        for (GymMember member : members) {

            for (Map.Entry<DayOfWeek, Workout> training : member.getTrainingProgram().entrySet()) {

                for (Exercise ex : training.getValue().exercises()) {

                    if (ex.name().equals(exerciseName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null || exerciseName.isBlank() || exerciseName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Map<DayOfWeek, List<String>> result = new HashMap<>();

        for (GymMember member : members) {

            for (Map.Entry<DayOfWeek, Workout> training : member.getTrainingProgram().entrySet()) {

                if (training == null) {
                    continue;
                }

                for (Exercise ex : training.getValue().exercises()) {

                    if (ex.name() != null && ex.name().equals(exerciseName)) {
                        result.putIfAbsent(training.getKey(), new ArrayList<>());
                        result.get(training.getKey()).add(member.getName());
                        break;
                    }
                }
            }
        }

        return Collections.unmodifiableMap(result);
    }

    public class MemberByProximityComparator implements Comparator<GymMember> {

        @Override
        public int compare(GymMember first, GymMember second) {
            double firstProximity = address.getDistanceTo(first.getAddress());
            double secondProximity = address.getDistanceTo(second.getAddress());

            return Double.compare(firstProximity, secondProximity);
        }
    }
}