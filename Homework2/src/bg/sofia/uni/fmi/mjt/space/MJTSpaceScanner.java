package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private final Rijndael rijndael;
    private final List<Mission> missions;
    private final List<Rocket> rockets;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        rijndael = new Rijndael(secretKey);
        missions = new ArrayList<>();
        rockets = new ArrayList<>();

        try (var sc = new Scanner(missionsReader)) {
            sc.nextLine();

            while (sc.hasNextLine()) {
                String missionInfo = sc.nextLine();
                missions.add(Mission.of(missionInfo));
            }
        }

        try (var sc = new Scanner(rocketsReader)) {
            sc.nextLine();

            while (sc.hasNextLine()) {
                String rocketInfo = sc.nextLine();
                rockets.add(Rocket.of(rocketInfo));
            }
        }
    }

    @Override
    public Collection<Mission> getAllMissions() {

        return missions.stream().toList();
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null!");
        }

        return missions.stream().filter(m -> m.missionStatus().equals(missionStatus)).toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Provided local date data cannot be null!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To local date cannot be before from local date!");
        }

        return missions.stream().filter(m -> m.date().isAfter(from) && m.date().isBefore(to))
            .filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS))
            .collect(Collectors.groupingBy(Mission::company, Collectors.counting())).entrySet().stream()
            .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("");
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
            .collect(Collectors.groupingBy(m -> getCountry(m.location()), Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("Cost bound cannot be zero or less!");
        }

        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null!");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null!");
        }

        return missions.stream()
            .filter(m -> m.missionStatus().equals(missionStatus) && m.rocketStatus().equals(rocketStatus))
            .filter(m -> m.cost().isPresent()).sorted(Comparator.comparing(m -> m.cost().get())).limit(n).toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return getMostEncounteredLocationPerCompany(missions);
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Provided local date data cannot be null!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To local date cannot be before from local date!");
        }

        var successfulMissionsInPeriod = missions.stream().filter(m -> m.date().isAfter(from) && m.date().isBefore(to))
            .filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS)).toList();

        return getMostEncounteredLocationPerCompany(successfulMissionsInPeriod);
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return Collections.unmodifiableCollection(rockets);
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Cost bound cannot be zero or less!");
        }

        return rockets.stream().filter(r -> r.height().isPresent())
            .sorted(Comparator.comparing((Rocket r) -> r.height().get()).reversed()).limit(n).toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream().collect(Collectors.toUnmodifiableMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("Cost bound cannot be zero or less!");
        }

        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null!");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null!");
        }

        return missions.stream()
            .filter(m -> m.missionStatus().equals(missionStatus) && m.rocketStatus().equals(rocketStatus))
            .map(m -> new AbstractMap.SimpleEntry<>(getRocketByName(m.detail().rocketName()), m.cost()))
            .filter(e -> e.getKey().isPresent() && e.getValue().isPresent())
            .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().get(), e.getValue().get()))
            .filter(e -> e.getKey().wiki().isPresent()).sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .limit(n).map(e -> e.getKey().wiki().get()).toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream cannot be null!");
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Provided local date data cannot be null!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To local date cannot be before from local date!");
        }

        var rocketEntry =
            rockets.stream().map(r -> new AbstractMap.SimpleEntry<>(r.name(), getRocketReliability(r.name())))
                .max((re1, re2) -> re1.getValue().compareTo(re2.getValue()));

        String rocketName = rocketEntry.isPresent() ?
            rocketEntry.get().getValue().compareTo(0.0) != 0 ? rocketEntry.get().getKey() : "" : "";

        try (var br = new ByteArrayInputStream(rocketName.getBytes(StandardCharsets.UTF_8))) {
            rijndael.encrypt(br, outputStream);
        } catch (Throwable e) {
            throw new CipherException(e.getMessage(), e);
        }
    }

    private String getCountry(String location) {
        return Arrays.stream(location.split(", ")).toList().getLast();
    }

    private Map<String, String> getMostEncounteredLocationPerCompany(List<Mission> missions) {
        return missions.stream().map(m -> new AbstractMap.SimpleEntry<>(m.company(), m.location()))
            .collect(Collectors.groupingBy(e -> Arrays.asList(e.getKey(), e.getValue()), Collectors.counting()))
            .entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().getFirst(),
                Collectors.collectingAndThen(Collectors.maxBy(Map.Entry.comparingByValue()),
                    e -> e.map(compLocTuple -> compLocTuple.getKey().getLast()).orElse(null))));
    }

    private Optional<Rocket> getRocketByName(String rocketName) {
        return rockets.stream().filter(r -> r.name().equals(rocketName)).findAny();
    }

    private double getRocketReliability(String rocketName) {
        List<Mission> rocketMissions =
            missions.stream().filter(m -> m.detail().rocketName().equals(rocketName)).toList();

        List<Mission> successfulMissions =
            rocketMissions.stream().filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS)).toList();

        long missionsCount = rocketMissions.size();
        long successfulMissionsCount = successfulMissions.size();
        long unsuccessfulMissionsCount = missionsCount - successfulMissionsCount;

        if (missionsCount == 0) {
            return 0.0;
        }

        return (double) (2 * successfulMissionsCount + unsuccessfulMissionsCount) / (2 * missionsCount);
    }
}
