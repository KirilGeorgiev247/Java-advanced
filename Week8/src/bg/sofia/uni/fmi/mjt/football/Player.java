package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record Player(String name, String fullName, LocalDate birthDate, int age, double heightCm, double weightKg,
                     List<Position> positions, String nationality, int overallRating, int potential, long valueEuro,
                     long wageEuro, Foot preferredFoot) {

    private static final int SIMILARITY_MIN_DIFF = 3;

    public static Player of(String line) {
        return PlayerParser.extractPlayer(line);
    }

    public double getProspect() {
        return (double) (overallRating + potential) / age;
    }

    public boolean similarTo(Player other) {
        boolean hasCommonPosition = !Collections.disjoint(positions, other.positions());
        boolean hasSamePreferredFoot = preferredFoot.equals(other.preferredFoot());
        boolean haveSimilarOverall = Math.abs(overallRating - other.overallRating()) <= SIMILARITY_MIN_DIFF;

        return hasCommonPosition && hasSamePreferredFoot && haveSimilarOverall;
    }
}
