package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerParser {
    private static final int NAME_INDEX = 0;
    private static final int FULL_NAME_INDEX = 1;
    private static final int BIRTHDATE_INDEX = 2;
    private static final int AGE_INDEX = 3;
    private static final int HEIGHT_INDEX = 4;
    private static final int WEIGHT_INDEX = 5;
    private static final int POSITIONS_INDEX = 6;
    private static final int NATIONALITY_INDEX = 7;
    private static final int OVERALL_RATING_INDEX = 8;
    private static final int POTENTIAL_INDEX = 9;
    private static final int VALUE_INDEX = 10;
    private static final int WAGE_INDEX = 11;
    private static final int PREFERRED_FOOT_INDEX = 12;
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    public static Player extractPlayer(String info) {
        String[] props = info.split(";");

        String name = props[NAME_INDEX];
        String fullName = props[FULL_NAME_INDEX];
        LocalDate birthDate = LocalDate.parse(props[BIRTHDATE_INDEX], dateTimeFormatter);
        int age = Integer.parseInt(props[AGE_INDEX]);
        double heightCm = Double.parseDouble(props[HEIGHT_INDEX]);
        double weightKg = Double.parseDouble(props[WEIGHT_INDEX]);
        List<Position> positions =
            Arrays.stream(props[POSITIONS_INDEX].split(","))
                .map(Position::valueOf).collect(Collectors.toList());
        String nationality = props[NATIONALITY_INDEX];
        int overallRating = Integer.parseInt(props[OVERALL_RATING_INDEX]);
        int potential = Integer.parseInt(props[POTENTIAL_INDEX]);
        long valueEuro = Long.parseLong(props[VALUE_INDEX]);
        long wageEuro = Long.parseLong(props[WAGE_INDEX]);
        Foot preferredFoot = props[PREFERRED_FOOT_INDEX].equals("Left") ? Foot.LEFT : Foot.RIGHT;

        return new Player(name, fullName, birthDate, age, heightCm,
            weightKg, positions, nationality, overallRating,
            potential, valueEuro, wageEuro, preferredFoot);
    }
}
