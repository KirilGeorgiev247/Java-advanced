package bg.sofia.uni.fmi.mjt.space.parser;

import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

// TODO: ask if data is in valid format
public class MissionParser {
    private static final int ID_INDEX = 0;
    private static final int COMPANY_INDEX = 1;
    private static final int LOCATION_INDEX = 2;
    private static final int DATE_INDEX = 3;
    private static final int DETAIL_INDEX = 4;
    private static final int ROCKET_STATUS_INDEX = 5;
    private static final int COST_INDEX = 6;
    private static final int MISSION_STATUS_INDEX = 7;

    private static final String propsRegexPattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy",
        Locale.ENGLISH);

    public static Mission extractMission(String info) {
        String[] props = info.split(propsRegexPattern);

        String id = props[ID_INDEX];
        String companyName = props[COMPANY_INDEX];
        String location = props[LOCATION_INDEX].replace("\"", "");
        LocalDate date =
            LocalDate.parse(props[DATE_INDEX].replace("\"", ""), dateTimeFormatter);
        Detail detail = Detail.of(props[DETAIL_INDEX]);
        RocketStatus rocketStatus = getRocketStatus(props[ROCKET_STATUS_INDEX]);
        Optional<Double> cost = Optional.empty();
        if (props[COST_INDEX] != null && !props[COST_INDEX].isEmpty() && !props[COST_INDEX].isBlank()) {
            try {
                cost = Optional.of(Double.parseDouble(props[COST_INDEX].replace("\"", "")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cost format is invalid!", e);
            }
        }
        MissionStatus missionStatus = getMissionStatus(props[MISSION_STATUS_INDEX]);

        return new Mission(id, companyName, location, date, detail, rocketStatus, cost, missionStatus);
    }

    private static RocketStatus getRocketStatus(String rocketStatus) {
        if (rocketStatus.equals(RocketStatus.STATUS_ACTIVE.toString())) {
            return RocketStatus.STATUS_ACTIVE;
        }

        return RocketStatus.STATUS_RETIRED;
    }

    private static MissionStatus getMissionStatus(String missionStatus) {
        return Arrays.stream(MissionStatus.values())
            .filter(ms -> ms.toString().equals(missionStatus))
            .findFirst()
            .orElse(MissionStatus.PRELAUNCH_FAILURE);
    }
}
