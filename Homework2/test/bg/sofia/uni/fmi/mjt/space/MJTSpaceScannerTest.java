package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.parser.MissionParser;
import bg.sofia.uni.fmi.mjt.space.parser.RocketParser;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MJTSpaceScannerTest {

    private static final DateTimeFormatter dateTimeFormatter =
        DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH);
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;
    private final String allMissionsTestInput = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
        6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Jul 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
        7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
        8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
        9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Success
        10,Northrop,"LP-0B, Wallops Flight Facility, Virginia, USA","Wed Jul 15, 2020",Minotaur IV | NROL-129,StatusActive,"46.0 ",Success
        11,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Fri Jul 10, 2020","Kuaizhou 11 | Jilin-1 02E, CentiSpace-1 S2",StatusActive,"28.3 ",Failure
        12,CASC,"LC-3, Xichang Satellite Launch Center, China","Thu Jul 09, 2020",Long March 3B/E | Apstar-6D,StatusActive,"29.15 ",Success
        13,IAI,"Pad 1, Palmachim Airbase, Israel","Mon Jul 06, 2020",Shavit-2 | Ofek-16,StatusActive,,Success
        14,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Jul 04, 2020",Long March 2D | Shiyan-6 02,StatusActive,"29.75 ",Success
        15,Rocket Lab,"Rocket Lab LC-1A, M?hia Peninsula, New Zealand","Sat Jul 04, 2020",Electron/Curie | Pics Or It Didn??¦t Happen,StatusActive,"7.5 ",Failure
        16,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Fri Jul 03, 2020",Long March 4B | Gaofen Duomo & BY-02,StatusActive,"64.68 ",Success
        17,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Tue Jun 30, 2020",Falcon 9 Block 5 | GPS III SV03,StatusActive,"50.0 ",Success
        18,CASC,"LC-2, Xichang Satellite Launch Center, China","Tue Jun 23, 2020",Long March 3B/E | Beidou-3 G3,StatusActive,"29.15 ",Success
        29,MHI,"LA-Y2, Tanegashima Space Center, Japan","Wed May 20, 2020",H-IIB | HTV-9,StatusRetired,"112.5 ",Success""";
    private final String allMissionsByStatusTestInput = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        11,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Fri Jul 10, 2020","Kuaizhou 11 | Jilin-1 02E, CentiSpace-1 S2",StatusActive,"28.3 ",Failure
        15,Rocket Lab,"Rocket Lab LC-1A, M?hia Peninsula, New Zealand","Sat Jul 04, 2020",Electron/Curie | Pics Or It Didn??¦t Happen,StatusActive,"7.5 ",Failure""";
    private final String noMissionsTestInput =
        "Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket,\" Rocket\",Status Mission";
    private final String missionsByCountryTestInput = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
        6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Jul 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success""";
    private final String usaFirstMissionTestInput =
        "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success";
    private final String usaSecondMissionTestInput =
        "2,SpaceX,\"Pad A, Boca Chica, Texas, USA\",\"Tue Aug 04, 2020\",Starship Prototype | 150 Meter Hop,StatusActive,,Success";
    private final String usaThirdMissionTestInput =
        "4,ULA,\"SLC-41, Cape Canaveral AFS, Florida, USA\",\"Thu Jul 30, 2020\",Atlas V 541 | Perseverance,StatusActive,\"145.0 \",Success";
    private final String chinaFirstMissionTestInput =
        "1,CASC,\"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China\",\"Thu Aug 06, 2020\",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,\"29.75 \",Success";
    private final String chinaSecondMissionTestInput =
        "5,CASC,\"LC-9, Taiyuan Satellite Launch Center, China\",\"Sat Jul 25, 2020\",\"Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1\",StatusActive,\"64.68 \",Success";
    private final String kazakhstanFirstMissionTestInput =
        "3,Roscosmos,\"Site 200/39, Baikonur Cosmodrome, Kazakhstan\",\"Thu Jul 30, 2020\",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,\"65.0 \",Success";
    private final String kazakhstanSecondMissionTestInput =
        "6,Roscosmos,\"Site 31/6, Baikonur Cosmodrome, Kazakhstan\",\"Thu Jul 23, 2020\",Soyuz 2.1a | Progress MS-15,StatusActive,\"48.5 \",Success";
    private final String firstLeastExpensiveMissionTestInput =
        "12,CASC,\"LC-3, Xichang Satellite Launch Center, China\",\"Thu Jul 09, 2020\",Long March 3B/E | Apstar-6D,StatusActive,\"29.15 \",Success";
    private final String secondLeastExpensiveMissionTestInput =
        "18,CASC,\"LC-2, Xichang Satellite Launch Center, China\",\"Tue Jun 23, 2020\",Long March 3B/E | Beidou-3 G3,StatusActive,\"29.15 \",Success";
    private final String thirdLeastExpensiveMissionTestInput =
        "1,CASC,\"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China\",\"Thu Aug 06, 2020\",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,\"29.75 \",Success";
    private final String leastExpensiveMissionsTestInput = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        12,CASC,"LC-3, Xichang Satellite Launch Center, China","Thu Jul 09, 2020",Long March 3B/E | Apstar-6D,StatusActive,"29.15 ",Success
        18,CASC,"LC-2, Xichang Satellite Launch Center, China","Tue Jun 23, 2020",Long March 3B/E | Beidou-3 G3,StatusActive,"29.15 ",Success""";
    private final String mostDesiredLocationPerCompanyTestInput = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"Resilovo, Bulgaria","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Failure
        1,CASC,"Chiprovtsi, Bulgaria","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
        2,SpaceX,"Sofia, Bulgaria","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
        5,CASC,"Chiprovtsi, Bulgaria","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
        7,CASC,"Sofia, Bulgaria","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
        8,SpaceX,"Resilovo, Bulgaria","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Failure
        13,IAI,"Pad 1, Palmachim Airbase, Israel","Mon Jul 06, 2020",Shavit-2 | Ofek-16,StatusActive,,Success""";
    private final String noRocketsTestInput = "\"\",Name,Wiki,Rocket Height";
    private final String allRocketsTestInput = """
        "",Name,Wiki,Rocket Height
        0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
        2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
        3,Unha-3,https://en.wikipedia.org/wiki/Unha,32.0 m
        4,Vanguard,https://en.wikipedia.org/wiki/Vanguard_(rocket),23.0 m
        5,Vector-H,https://en.wikipedia.org/wiki/Vector-H,18.3 m
        6,Vector-R,https://en.wikipedia.org/wiki/Vector-R,13.0 m
        7,Vega,https://en.wikipedia.org/wiki/Vega_(rocket),29.9 m
        8,Vega C,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        9,Vega E,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        10,VLS-1,https://en.wikipedia.org/wiki/VLS-1,19.0 m
        11,Volna,https://en.wikipedia.org/wiki/Volna,15.0 m
        12,Voskhod,https://en.wikipedia.org/wiki/Voskhod_(rocket),31.0 m
        13,Vostok,https://en.wikipedia.org/wiki/Vostok-K,31.0 m
        14,Vostok-2,https://en.wikipedia.org/wiki/Vostok-2_(rocket),
        15,Vostok-2A,https://en.wikipedia.org/wiki/Vostok_(rocket_family),
        16,Vostok-2M,https://en.wikipedia.org/wiki/Vostok-2M,
        17,Vulcan Centaur,https://en.wikipedia.org/wiki/Vulcan_%28rocket%29,58.3 m
        18,Zenit-2,https://en.wikipedia.org/wiki/Zenit-2,57.0 m""";
    private final String tallestRocketsTestInput = """
        "",Name,Wiki,Rocket Height
        17,Vulcan Centaur,https://en.wikipedia.org/wiki/Vulcan_%28rocket%29,58.3 m
        16,Vostok-2M,https://en.wikipedia.org/wiki/Vostok-2M,""";
    private final String wikiPageForRocketTestInput = """
        "",Name,Wiki,Rocket Height
        0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Tsyklon-4M,,38.7 m
        2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m""";
    private final String firstTallestRocketTestInput =
        "17,Vulcan Centaur,https://en.wikipedia.org/wiki/Vulcan_%28rocket%29,58.3 m";
    private final String secondTallestRocketTestInput = "18,Zenit-2,https://en.wikipedia.org/wiki/Zenit-2,57.0 m";
    private final String thirdTallestRocketTestInput = "0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m";
    private final String wikiPagesForRocketsTestMissionsInput = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Rocket One | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Rocket Two | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Rocket Three | 150 Meter Hop,StatusActive,,Success
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Rocket Four | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success""";
    private final String wikiPagesForRocketsTestRocketsInput = """
        "",Name,Wiki,Rocket Height
        0,Rocket One,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Rocket Two,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
        2,Rocket Three,https://en.wikipedia.org/wiki/Unha,28.0 m
        3,Rocket Four,https://en.wikipedia.org/wiki/Unha,32.0 m""";
    private final String mostReliableRocketTestMissionsInput = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Rocket One | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Rocket Two | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Rocket Two | 150 Meter Hop,StatusActive,,Failure
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Rocket Two | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success""";
    private final String mostReliableRocketTestRocketsInput = """
        "",Name,Wiki,Rocket Height
        0,Rocket One,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Rocket Two,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m""";
    private final LocalDate fromTestInput = LocalDate.parse("Tue Jun 23, 2020", dateTimeFormatter);
    private final LocalDate toTestInput = LocalDate.parse("Fri Aug 07, 2020", dateTimeFormatter);
    @Mock
    private SecretKey secretKeyMock;

    @Test
    void testIfMissionsAreLoadedCorrectly() {
        List<Mission> expectedResult = new ArrayList<>();
        Collection<Mission> actualResult;

        try (var missionReader = new StringReader(allMissionsTestInput)) {

            Scanner sc = new Scanner(missionReader);

            sc.nextLine();

            while (sc.hasNextLine()) {
                expectedResult.add(Mission.of(sc.nextLine()));
            }

            sc.close();
        }

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getAllMissions();
        }

        assertIterableEquals(expectedResult, actualResult, "Missions dataset is not loader correctly!");
    }

    @Test
    void testIfGetAllMissionsByStatusThrowsWhenMissionStatusIsInvalid() {
        SpaceScannerAPI spaceScanner;
        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getAllMissions(null),
            "Get all missions by status should throw when invalid mission status!");
    }

    @Test
    void testIfGetAllMissionsByStatusReturnsCorrectly() {
        List<Mission> expectedResult = new ArrayList<>();
        Collection<Mission> actualResult;

        try (var missionReader = new StringReader(allMissionsByStatusTestInput)) {

            Scanner sc = new Scanner(missionReader);

            sc.nextLine();

            while (sc.hasNextLine()) {
                expectedResult.add(Mission.of(sc.nextLine()));
            }

            sc.close();
        }

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getAllMissions(MissionStatus.FAILURE);
        }

        assertIterableEquals(expectedResult, actualResult, "Get all mission by status returns wrong info!");
    }

    @Test
    void testIfGetAllMissionsByStatusReturnsEmptyCollectionWhenNoMissions() {
        Collection<Mission> actualResult;

        try (var missionReader = new StringReader(allMissionsByStatusTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getAllMissions(MissionStatus.SUCCESS);
        }

        assertTrue(actualResult.isEmpty(), "Get all missions by status should return empty collection if no missions!");
    }

    @Test
    void testIfGetCompanyWithMostSuccessfulMissionsThrowsWhenInvalidData() {
        SpaceScannerAPI spaceScanner;

        try (var missionReader = new StringReader(allMissionsByStatusTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getCompanyWithMostSuccessfulMissions(null, toTestInput),
            "Get company with most successful missions should throw when from local date is null!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getCompanyWithMostSuccessfulMissions(fromTestInput, null),
            "Get company with most successful missions should throw when to local date is null!");
        assertThrows(TimeFrameMismatchException.class,
            () -> spaceScanner.getCompanyWithMostSuccessfulMissions(toTestInput, fromTestInput),
            "Get company with most successful missions should throw when to local date is before from local date!");
    }

    @Test
    void testIfGetCompanyWithMostSuccessfulMissionsReturnsCorrectly() {
        String expectedResult = "CASC";
        String actualResult;

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getCompanyWithMostSuccessfulMissions(fromTestInput, toTestInput);
        }

        assertEquals(expectedResult, actualResult,
            "Get company with most successful missions should return the right company!");
    }

    @Test
    void testIfGetCompanyWithMostSuccessfulMissionsReturnsEmptyStringIfNoMissions() {
        String expectedResult = "";
        String actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getCompanyWithMostSuccessfulMissions(fromTestInput, toTestInput);
        }

        assertEquals(expectedResult, actualResult,
            "Get company with most successful missions should return empty when no missions!");
    }

    @Test
    void testIfGetMissionsPerCountryReturnsCorrectly() {
        String usa = "USA";
        String china = "China";
        String kazakhstan = "Kazakhstan";

        Mission usaFirstMission = MissionParser.extractMission(usaFirstMissionTestInput);
        Mission usaSecondMission = MissionParser.extractMission(usaSecondMissionTestInput);
        Mission usaThirdMission = MissionParser.extractMission(usaThirdMissionTestInput);
        Mission chinaFirstMission = MissionParser.extractMission(chinaFirstMissionTestInput);
        Mission chinaSecondMission = MissionParser.extractMission(chinaSecondMissionTestInput);
        Mission kazakhstanFirstMission = MissionParser.extractMission(kazakhstanFirstMissionTestInput);
        Mission kazakhstanSecondMission = MissionParser.extractMission(kazakhstanSecondMissionTestInput);

        Map<String, Collection<Mission>> expectedResult =
            Map.of(usa, List.of(usaFirstMission, usaSecondMission, usaThirdMission), china,
                List.of(chinaFirstMission, chinaSecondMission), kazakhstan,
                List.of(kazakhstanFirstMission, kazakhstanSecondMission));

        Map<String, Collection<Mission>> actualResult;

        try (var missionReader = new StringReader(missionsByCountryTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getMissionsPerCountry();
        }

        assertEquals(expectedResult, actualResult, "Get missions per country should return correctly!");
    }

    @Test
    void testIfGetMissionsPerCountryReturnsEmptyWhenNoMissions() {
        Map<String, Collection<Mission>> expectedResult = Map.of();
        Map<String, Collection<Mission>> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getMissionsPerCountry();
        }

        assertEquals(expectedResult, actualResult, "Get missions per country should return empty when no missions!");
    }

    @Test
    void testIfGetTopNLeastExpensiveMissionsThrowsWhenInvalidData() {
        SpaceScannerAPI spaceScanner;

        try (var missionReader = new StringReader(allMissionsByStatusTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get top n least expensive missions should throw when n is zero!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getTopNLeastExpensiveMissions(-1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get top n least expensive missions should throw when n is less than zero!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getTopNLeastExpensiveMissions(5, null, RocketStatus.STATUS_ACTIVE),
            "Get top n least expensive missions should throw when mission status is null!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getTopNLeastExpensiveMissions(5, MissionStatus.SUCCESS, null),
            "Get top n least expensive missions should throw when rocket status is null!");
    }

    @Test
    void testIfGetTopNLeastExpensiveMissionsReturnsCorrectly() {
        Mission firstLeastExpensiveMission = MissionParser.extractMission(firstLeastExpensiveMissionTestInput);
        Mission secondLeastExpensiveMission = MissionParser.extractMission(secondLeastExpensiveMissionTestInput);
        Mission thirdLeastExpensiveMission = MissionParser.extractMission(thirdLeastExpensiveMissionTestInput);

        List<Mission> expectedResult =
            List.of(firstLeastExpensiveMission, secondLeastExpensiveMission, thirdLeastExpensiveMission);

        List<Mission> actualResult;

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult =
                spaceScanner.getTopNLeastExpensiveMissions(3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
        }

        assertIterableEquals(expectedResult, actualResult,
            "Get top n least expensive mission should return correctly!");
    }

    @Test
    void testIfGetTopNLeastExpensiveMissionsReturnsCorrectlyWhenNIsMoreThanTheCount() {
        Mission firstLeastExpensiveMission = MissionParser.extractMission(firstLeastExpensiveMissionTestInput);
        Mission secondLeastExpensiveMission = MissionParser.extractMission(secondLeastExpensiveMissionTestInput);

        List<Mission> expectedResult = List.of(firstLeastExpensiveMission, secondLeastExpensiveMission);

        List<Mission> actualResult;

        try (var missionReader = new StringReader(leastExpensiveMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult =
                spaceScanner.getTopNLeastExpensiveMissions(3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
        }

        assertIterableEquals(expectedResult, actualResult,
            "Get top n least expensive mission should return correctly even when n is more than the missions encountered!");
    }

    @Test
    void testIfGetTopNLeastExpensiveMissionsReturnsEmptyWhenNoMissions() {
        List<Mission> expectedResult = List.of();

        List<Mission> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult =
                spaceScanner.getTopNLeastExpensiveMissions(3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
        }

        assertIterableEquals(expectedResult, actualResult,
            "Get top n least expensive mission should return empty when no missions!");
    }

    @Test
    void testIfGetMostDesiredLocationForMissionsPerCompanyReturnsCorrectly() {
        String spaceXCompanyName = "SpaceX";
        String cascCompanyName = "CASC";
        String iaiCompanyName = "IAI";
        String chiprovtsiLocation = "Chiprovtsi, Bulgaria";
        String resilovoLocation = "Resilovo, Bulgaria";
        String padLocation = "Pad 1, Palmachim Airbase, Israel";

        Map<String, String> expectedResult =
            Map.of(spaceXCompanyName, resilovoLocation, cascCompanyName, chiprovtsiLocation, iaiCompanyName,
                padLocation);

        Map<String, String> actualResult;

        try (var missionReader = new StringReader(mostDesiredLocationPerCompanyTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getMostDesiredLocationForMissionsPerCompany();
        }

        assertEquals(expectedResult, actualResult,
            "Get most desired location for missions per company should return correctly!");
    }

    @Test
    void testIfGetMostDesiredLocationForMissionsPerCountryReturnsEmptyWhenNoMissions() {
        Map<String, String> expectedResult = Map.of();
        Map<String, String> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getMostDesiredLocationForMissionsPerCompany();
        }

        assertEquals(expectedResult, actualResult,
            "Get most desired location for missions per company should return empty when no missions!");
    }

    @Test
    void testIfGetLocationWithMostSuccessfulMissionsPerCompanyThrowsWhenInvalidData() {
        SpaceScannerAPI spaceScanner;

        try (var missionReader = new StringReader(allMissionsByStatusTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(null, toTestInput),
            "Get location with most successful missions per company should throw when from local date is null!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(fromTestInput, null),
            "Get location with most successful missions per company should throw when to local date is null!");
        assertThrows(TimeFrameMismatchException.class,
            () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(toTestInput, fromTestInput),
            "Get location with most successful missions per company should throw when to local date is before from local date!");
    }

    @Test
    void testIfGetLocationWithMostSuccessfulMissionsPerCompanyReturnsCorrectly() {
        String spaceXCompanyName = "SpaceX";
        String cascCompanyName = "CASC";
        String iaiCompanyName = "IAI";
        String sofiaLocation = "Sofia, Bulgaria";
        String padLocation = "Pad 1, Palmachim Airbase, Israel";

        Map<String, String> expectedResult =
            Map.of(spaceXCompanyName, sofiaLocation, cascCompanyName, sofiaLocation, iaiCompanyName, padLocation);

        Map<String, String> actualResult;

        try (var missionReader = new StringReader(mostDesiredLocationPerCompanyTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(fromTestInput, toTestInput);
        }

        assertEquals(expectedResult, actualResult,
            "Get location with most successful missions per company should return correctly!");
    }

    @Test
    void testIfGetLocationWithMostSuccessfulMissionsPerCompanyReturnsEmptyWhenNoMissions() {
        Map<String, String> expectedResult = Map.of();
        Map<String, String> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(fromTestInput, toTestInput);
        }

        assertEquals(expectedResult, actualResult,
            "Get location with most successful missions per company should return empty when no missions!");
    }

    @Test
    void testIfGetAllRocketsReturnsCorrectly() {
        List<Rocket> expectedResult = new ArrayList<>();
        Collection<Rocket> actualResult;

        try (var rocketReader = new StringReader(allRocketsTestInput)) {

            Scanner sc = new Scanner(rocketReader);

            sc.nextLine();

            while (sc.hasNextLine()) {
                expectedResult.add(Rocket.of(sc.nextLine()));
            }

            sc.close();
        }

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getAllRockets();
        }

        assertIterableEquals(expectedResult, actualResult, "Rockets dataset is not loader correctly!");
    }

    @Test
    void testIfGetTopNTallestRocketsThrowsWhenInvalidData() {
        SpaceScannerAPI spaceScanner;

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNTallestRockets(0),
            "Get top n tallest rockets throws when n is zero!");
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNTallestRockets(-5),
            "Get top n tallest rockets throws when n is less than zero!");
    }

    @Test
    void testIfGetTopNTallestRocketsReturnsCorrectly() {
        Rocket firstTallestRocket = RocketParser.extractRocket(firstTallestRocketTestInput);
        Rocket secondTallestRocket = RocketParser.extractRocket(secondTallestRocketTestInput);
        Rocket thirdTallestRocket = RocketParser.extractRocket(thirdTallestRocketTestInput);

        List<Rocket> expectedResult = List.of(firstTallestRocket, secondTallestRocket, thirdTallestRocket);
        List<Rocket> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getTopNTallestRockets(3);
        }

        assertIterableEquals(expectedResult, actualResult, "Get top n tallest rockets should return correctly!");
    }

    @Test
    void testIfGetTopNTallestRocketsReturnsEmptyWhenNoRockets() {
        SpaceScannerAPI spaceScanner;
        List<Rocket> actualResult;

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
            actualResult = spaceScanner.getTopNTallestRockets(5);
        }

        assertTrue(actualResult.isEmpty(), "Get top n tallest rockets should return empty when no rockets!");
    }

    @Test
    void testIfGetTopNTallestRocketsReturnsCorrectlyEvenIfNIsBiggerThanCount() {
        Rocket firstTallestRocket = RocketParser.extractRocket(firstTallestRocketTestInput);

        List<Rocket> expectedResult = List.of(firstTallestRocket);
        List<Rocket> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(tallestRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getTopNTallestRockets(3);
        }

        assertIterableEquals(expectedResult, actualResult,
            "Get top n tallest rockets should return correctly even if n is bigger than count!");
    }

    @Test
    void testIfGetWikiPageForRocketReturnsCorrectly() {
        String firstRocketName = "Tsyklon-3";
        String secondRocketName = "Tsyklon-4M";
        String thirdRocketName = "Unha-2";
        Optional<String> firstRocketWiki = Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3");
        Optional<String> secondRocketWiki = Optional.empty();
        Optional<String> thirdRocketWiki = Optional.of("https://en.wikipedia.org/wiki/Unha");

        Map<String, Optional<String>> expectedResult =
            Map.of(firstRocketName, firstRocketWiki, secondRocketName, secondRocketWiki, thirdRocketName,
                thirdRocketWiki);

        Map<String, Optional<String>> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(wikiPageForRocketTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);

            actualResult = spaceScanner.getWikiPageForRocket();
        }

        assertEquals(expectedResult, actualResult, "Get wiki page for rocket should return correctly!");
    }

    @Test
    void testIfGetWikiPageForRocketReturnsEmptyWhenNoRockets() {
        SpaceScannerAPI spaceScanner;
        Map<String, Optional<String>> actualResult;

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
            actualResult = spaceScanner.getWikiPageForRocket();
        }

        assertTrue(actualResult.isEmpty(), "Get wiki page for rocket should return empty when no rockets!");
    }

    @Test
    void testIfGetWikiPagesForRocketsUsedInMostExpensiveMissionsThrowsWhenInvalidData() {
        SpaceScannerAPI spaceScanner;

        try (var missionReader = new StringReader(allMissionsByStatusTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE),
            "Get wiki pages for rockets used in most expensive missions should throw when n is zero!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(-1, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE),
            "Get wiki pages for rockets used in most expensive missions should throw when n is less than zero!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(5, null, RocketStatus.STATUS_ACTIVE),
            "Get wiki pages for rockets used in most expensive missions should throw when mission status is null!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(5, MissionStatus.SUCCESS, null),
            "Get wiki pages for rockets used in most expensive missions should throw when rocket status is null!");
    }

    @Test
    void testIfGetWikiPagesForRocketsUsedInMostExpensiveMissionsReturnsCorrectly() {
        String firstRocketWiki = "https://en.wikipedia.org/wiki/Tsyklon-3";
        String forthRocketWiki = "https://en.wikipedia.org/wiki/Unha";
        List<String> expectedResult = List.of(forthRocketWiki, firstRocketWiki);
        List<String> actualResult;

        try (var missionReader = new StringReader(wikiPagesForRocketsTestMissionsInput);
             var rocketReader = new StringReader(wikiPagesForRocketsTestRocketsInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
            actualResult = spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE);
        }

        assertIterableEquals(expectedResult, actualResult,
            "Get wiki pages for rockets used in most expensive missions should return correctly!");
    }

    @Test
    void testIfGetWikiPagesForRocketsUsedInMostExpensiveMissionsReturnsEmptyWhenNoRockets() {
        SpaceScannerAPI spaceScanner;
        List<String> actualResult;

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(noRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
            actualResult = spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(3, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE);
        }

        assertTrue(actualResult.isEmpty(),
            "Get wiki pages for rockets used in most expensive missions should return empty when no rockets!");
    }

    @Test
    void testIfGetWikiPagesForRocketsUsedInMostExpensiveMissionsReturnsEmptyWhenNoMissions() {
        SpaceScannerAPI spaceScanner;
        List<String> actualResult;

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
            actualResult = spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(3, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE);
        }

        assertTrue(actualResult.isEmpty(),
            "Get wiki pages for rockets used in most expensive missions should return empty when no missions!");
    }

    @Test
    void testIfSaveMostReliableRocketThrowsWhenInvalidData() {
        OutputStream outputStreamMock = Mockito.mock(OutputStream.class);
        SpaceScannerAPI spaceScanner;

        try (var missionReader = new StringReader(allMissionsTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.saveMostReliableRocket(null, fromTestInput, toTestInput),
            "Save most reliable rocket should throw when output stream is null!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.saveMostReliableRocket(outputStreamMock, null, toTestInput),
            "Save most reliable rocket should throw when from local date is null!");
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.saveMostReliableRocket(outputStreamMock, fromTestInput, null),
            "Save most reliable rocket should throw when to local date is null!");
        assertThrows(TimeFrameMismatchException.class,
            () -> spaceScanner.saveMostReliableRocket(outputStreamMock, toTestInput, fromTestInput),
            "Save most reliable rocket should throw when to local date is before from local date!");
    }

    @Test
    void testIfSaveMostReliableRocketWorksCorrectly() throws CipherException {
        String mostReliableRocketName = "Rocket One";
        SecretKey secretKey = generateSecretKey();
        Rijndael rijndael = new Rijndael(secretKey);
        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();

        try (var missionReader = new StringReader(mostReliableRocketTestMissionsInput);
             var rocketReader = new StringReader(mostReliableRocketTestRocketsInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKey);
            spaceScanner.saveMostReliableRocket(encryptedOutputStream, fromTestInput, toTestInput);
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedOutputStream.toByteArray());
             ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream()) {

            rijndael.decrypt(inputStream, decryptedOutputStream);

            assertArrayEquals(mostReliableRocketName.getBytes(), decryptedOutputStream.toByteArray(),
                "Most reliable rocket name should be encrypted!");
        } catch (IOException e) {
            throw new UncheckedIOException("Problem with closing occurred!",e);
        }
    }

    @Test
    void testIfSaveMostReliableRocketEncryptsEmptyWhenNoRockets() throws CipherException {
        String mostReliableRocketName = "";
        SecretKey secretKey = generateSecretKey();
        Rijndael rijndael = new Rijndael(secretKey);
        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();

        try (var missionReader = new StringReader(noMissionsTestInput);
             var rocketReader = new StringReader(allRocketsTestInput)) {
            SpaceScannerAPI spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKey);
            spaceScanner.saveMostReliableRocket(encryptedOutputStream, fromTestInput, toTestInput);
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedOutputStream.toByteArray());
             ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream()) {

            rijndael.decrypt(inputStream, decryptedOutputStream);

            assertArrayEquals(mostReliableRocketName.getBytes(), decryptedOutputStream.toByteArray(),
                "Empty string should be encrypted when no missions!");
        } catch (IOException e) {
            throw new UncheckedIOException("Problem with closing occurred!",e);
        }
    }

    @Test
    void testIfSaveMostReliableRocketThrowsWhenInvalidKey() {
        SpaceScannerAPI spaceScanner;
        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();

        try (var missionReader = new StringReader(mostReliableRocketTestMissionsInput);
             var rocketReader = new StringReader(mostReliableRocketTestRocketsInput)) {
            spaceScanner = new MJTSpaceScanner(missionReader, rocketReader, secretKeyMock);
        }

        assertThrows(CipherException.class, () -> spaceScanner.saveMostReliableRocket(encryptedOutputStream, fromTestInput, toTestInput),
            "Cipher exception should be thrown when invalid secret key!");
    }

    private SecretKey generateSecretKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm", e);
        }
        keyGenerator.init(KEY_SIZE_IN_BITS);
        return keyGenerator.generateKey();
    }
}
