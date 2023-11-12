package bg.sofia.uni.fmi.mjt.gym.member;

import java.util.Comparator;

public class MemberByNameComparator implements Comparator<GymMember> {

    @Override
    public int compare(GymMember first, GymMember second) {
        return String.CASE_INSENSITIVE_ORDER.compare(first.getName(), second.getName());
    }

}
