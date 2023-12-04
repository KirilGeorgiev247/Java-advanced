package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FootballPlayerAnalyzerTest {

    String allPlayersTestInput = """
        name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
        L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
        C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
        P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
        L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right
        K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right
        V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
        K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right
        S. Agüero;Sergio Leonel Agüero del Castillo;6/2/1988;30;172.72;69.9;ST;Argentina;89;89;64500000;300000;Right
        M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Germany;89;89;38000000;130000;Right
        E. Cavani;Edinson Roberto Cavani Gómez;2/14/1987;32;185.42;77.1;ST;Uruguay;89;89;60000000;200000;Right
        Sergio Busquets;Sergio Busquets i Burgos;7/16/1988;30;187.96;76.2;CDM,CM;Spain;89;89;51500000;315000;Right
        T. Courtois;Thibaut Courtois;5/11/1992;26;198.12;96.2;GK;Belgium;89;90;53500000;240000;Left
        M. ter Stegen;Marc-André ter Stegen;4/30/1992;26;187.96;84.8;GK;Germany;89;92;58000000;240000;Right""";

    String nationalityTestInput = """
        name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
        L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
        C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
        P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
        L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right""";

    String highestPaidPlayerTestInput = """
        name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
        L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
        S. Agüero;Sergio Leonel Agüero del Castillo;6/2/1988;30;172.72;69.9;ST;Argentina;89;89;64500000;300000;Right
        P. Dybala;Paulo Bruno Exequiel Dybala;11/15/1993;25;152.4;74.8;CAM,RW;Argentina;89;94;89000000;205000;Left
        G. Higuaín;Gonzalo Gerardo Higuaín;12/10/1987;31;185.42;88.9;ST;Argentina;87;87;48500000;205000;Right
        I. Rakitić;Ivan Rakitić;3/10/1988;30;182.88;78;CM,CDM;Croatia;87;87;46500000;260000;Right
        J. Vertonghen;Jan Vertonghen;4/24/1987;31;187.96;86.2;CB;Belgium;87;87;34000000;155000;Left""";

    String prospectPlayerTestInput = """
        name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
        L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
        C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
        P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
        L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right
        K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right
        V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
        K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right
        S. Agüero;Sergio Leonel Agüero del Castillo;6/2/1988;30;172.72;69.9;ST;Argentina;89;89;64500000;300000;Right
        M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Germany;89;89;38000000;130000;Right
        E. Cavani;Edinson Roberto Cavani Gómez;2/14/1987;32;185.42;77.1;ST;Uruguay;89;89;60000000;200000;Right
        Sergio Busquets;Sergio Busquets i Burgos;7/16/1988;30;187.96;76.2;CDM,CM;Spain;89;89;51500000;315000;Right
        T. Courtois;Thibaut Courtois;5/11/1992;26;198.12;96.2;GK;Belgium;89;90;53500000;240000;Left
        M. ter Stegen;Marc-André ter Stegen;4/30/1992;26;187.96;84.8;GK;Germany;89;92;58000000;240000;Right
        A. Griezmann;Antoine Griezmann;3/21/1991;27;175.26;73;CF,ST;France;89;90;78000000;145000;Left
        M. Salah;Mohamed  Salah Ghaly;6/15/1992;26;175.26;71.2;RW,ST;Egypt;89;90;78500000;265000;Left
        P. Dybala;Paulo Bruno Exequiel Dybala;11/15/1993;25;152.4;74.8;CAM,RW;Argentina;89;94;89000000;205000;Left
        M. Škriniar;Milan Škriniar;2/11/1995;24;187.96;79.8;CB;Slovakia;86;93;53500000;89000;Right
        Fernandinho;Fernando Luiz Rosa;5/4/1985;33;152.4;67.1;CDM;Brazil;87;87;20500000;200000;Right
        G. Higuaín;Gonzalo Gerardo Higuaín;12/10/1987;31;185.42;88.9;ST;Argentina;87;87;48500000;205000;Right
        I. Rakitić;Ivan Rakitić;3/10/1988;30;182.88;78;CM,CDM;Croatia;87;87;46500000;260000;Right
        J. Vertonghen;Jan Vertonghen;4/24/1987;31;187.96;86.2;CB;Belgium;87;87;34000000;155000;Left""";


    FootballPlayerAnalyzer footballPlayerAnalyzer;

    @Test
    void testIfPlayersAreLoadedCorrectly() {
        try (var reader = new StringReader(allPlayersTestInput)) {
            reader.mark(allPlayersTestInput.length() + 1);

            Scanner sc = new Scanner(reader);

            sc.nextLine();
            List<Player> expectedResult = new ArrayList<>();

            while (sc.hasNextLine()) {
                expectedResult.add(Player.of(sc.nextLine()));
            }

            reader.reset();

            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            List<Player> actualResult = footballPlayerAnalyzer.getAllPlayers();

            assertIterableEquals(expectedResult, actualResult);

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    void testIfGetAllNationalitiesReturnsCorrect() {
        try (var reader = new StringReader(nationalityTestInput)) {
            reader.mark(nationalityTestInput.length() + 1);

            Scanner sc = new Scanner(reader);

            sc.nextLine();
            List<String> nationalities = new ArrayList<>();

            while (sc.hasNextLine()) {
                nationalities.add(Player.of(sc.nextLine()).nationality());
            }

            Set<String> expectedResult = new HashSet<>(nationalities);

            reader.reset();

            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            Set<String> actualResult = footballPlayerAnalyzer.getAllNationalities();

            assertIterableEquals(expectedResult, actualResult);

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    void testIfGetHighestPaidPlayerByNationalityThrowsWhenInvalidNationality() {
        try (var reader = new StringReader(highestPaidPlayerTestInput)) {
            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getHighestPaidPlayerByNationality(null),
                "Get highest paid player by nationality should throw when nationality is null!");

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getHighestPaidPlayerByNationality("  "),
                "Get highest paid player by nationality should throw when nationality is blank!");

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getHighestPaidPlayerByNationality(""),
                "Get highest paid player by nationality should throw when nationality is empty!");
        }
    }

    @Test
    void testIfGetHighestPaidPlayerByNationalityThrowsWhenNoSuchPlayerWithThisNationality() {
        try (var reader = new StringReader(highestPaidPlayerTestInput)) {
            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            assertThrows(NoSuchElementException.class,
                () -> footballPlayerAnalyzer.getHighestPaidPlayerByNationality("random"),
                "Get highest paid player by nationality should throw when no such player!");
        }
    }

    @Test
    void testIfGetHighestPaidPlayerByNationalityReturnsCorrectly() {
        try (var reader = new StringReader(highestPaidPlayerTestInput)) {
            reader.mark(highestPaidPlayerTestInput.length() + 1);

            Scanner sc = new Scanner(reader);

            sc.nextLine();
            List<Player> players = new ArrayList<>();

            while (sc.hasNextLine()) {
                players.add(Player.of(sc.nextLine()));
            }

            String nationality = players.getFirst().nationality();

            reader.reset();

            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            Player actualResult = footballPlayerAnalyzer.getHighestPaidPlayerByNationality(nationality);

            Player expectedResult = players.stream().filter(player -> player.nationality().equals(nationality))
                .max(Comparator.comparingDouble(Player::wageEuro)).orElse(null);

            assertEquals(expectedResult, actualResult);

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    void testIfGroupByPositionReturnsCorrectly() {
        try (var reader = new StringReader(allPlayersTestInput)) {
            reader.mark(allPlayersTestInput.length() + 1);

            Scanner sc = new Scanner(reader);
            sc.nextLine();

            List<Player> players = new ArrayList<>();

            while (sc.hasNextLine()) {
                players.add(Player.of(sc.nextLine()));
            }

            Map<Position, Set<Player>> expectedResult = players.stream().flatMap(
                    player -> player.positions().stream().map(position -> new AbstractMap.SimpleEntry<>(position, player)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));

            reader.reset();

            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            Map<Position, Set<Player>> actualResult = footballPlayerAnalyzer.groupByPosition();

            assertEquals(expectedResult, actualResult);

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    void testIfGetTopProspectPlayerForPositionInBudgetThrowsWhenInvalidPosition() {
        try (var reader = new StringReader(prospectPlayerTestInput)) {
            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getTopProspectPlayerForPositionInBudget(null, 0),
                "Get top prospect player for position in budget should throw when position is null!");
        }
    }

    @Test
    void testIfGetTopProspectPlayerForPositionInBudgetThrowsWhenInvalidBudget() {
        try (var reader = new StringReader(prospectPlayerTestInput)) {
            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getTopProspectPlayerForPositionInBudget(Position.CF, -5),
                "Get top prospect player for position in budget should throw when budget is negative!");
        }
    }

    @Test
    void testIfGetTopProspectPlayerForPositionInBudgetReturnsCorrectly() {
        try (var reader = new StringReader(prospectPlayerTestInput)) {
            reader.mark(prospectPlayerTestInput.length() + 1);

            Scanner sc = new Scanner(reader);
            sc.nextLine();

            List<Player> players = new ArrayList<>();

            while (sc.hasNextLine()) {
                players.add(Player.of(sc.nextLine()));
            }

            Position position = players.getFirst().positions().getFirst();
            Long budget = players.getFirst().valueEuro();

            Optional<Player> expectedResult =
                players.stream().filter(player -> player.positions().contains(position) && player.valueEuro() <= budget)
                    .max(Comparator.comparingDouble(Player::getProspect));

            reader.reset();

            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            Optional<Player> actualResult =
                footballPlayerAnalyzer.getTopProspectPlayerForPositionInBudget(position, budget);

            assertEquals(expectedResult, actualResult);

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    void testIfGetTopProspectPlayerForPositionInBudgetReturnsCorrectlyWhenNoPlayersMatchTheBudget() {
        try (var reader = new StringReader(prospectPlayerTestInput)) {
            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            Optional<Player> actualResult =
                footballPlayerAnalyzer.getTopProspectPlayerForPositionInBudget(Position.CF, 0);

            assertTrue(actualResult.isEmpty());
        }
    }

    @Test
    void testIfGetSimilarPlayersThrowsWhenPlayerIsNull() {
        try (var reader = new StringReader(allPlayersTestInput)) {
            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            assertThrows(IllegalArgumentException.class, () -> footballPlayerAnalyzer.getSimilarPlayers(null),
                "Get similar players should throw when player is null!");
        }
    }

    @Test
    void testIfGetSimilarPlayersReturnsCorrectly() {
        try (var reader = new StringReader(prospectPlayerTestInput)) {
            reader.mark(prospectPlayerTestInput.length() + 1);

            Scanner sc = new Scanner(reader);
            sc.nextLine();

            List<Player> players = new ArrayList<>();

            while (sc.hasNextLine()) {
                players.add(Player.of(sc.nextLine()));
            }

            Player player = players.getFirst();

            Set<Player> expectedResult =
                players.stream().filter(p -> p.similarTo(player)).collect(Collectors.toUnmodifiableSet());

            reader.reset();

            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            Set<Player> actualResult = footballPlayerAnalyzer.getSimilarPlayers(player);

            assertEquals(expectedResult, actualResult);

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    void testIfGetPlayersByFullNameKeywordThrowsWhenInvalidKeyword() {
        try (var reader = new StringReader(allPlayersTestInput)) {
            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getPlayersByFullNameKeyword(null),
                "Get players by full name by keyword should throw when keyword is null!");

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getPlayersByFullNameKeyword("  "),
                "Get players by full name by keyword should throw when keyword is blank!");

            assertThrows(IllegalArgumentException.class,
                () -> footballPlayerAnalyzer.getPlayersByFullNameKeyword(""),
                "Get players by full name by keyword should throw when keyword is empty!");
        }
    }

    @Test
    void testIfGetPlayersByFullNameKeywordReturnsCorrectly() {
        try (var reader = new StringReader(allPlayersTestInput)) {
            reader.mark(allPlayersTestInput.length() + 1);

            Scanner sc = new Scanner(reader);
            sc.nextLine();

            List<Player> players = new ArrayList<>();

            while (sc.hasNextLine()) {
                players.add(Player.of(sc.nextLine()));
            }

            Player player = players.getFirst();

            Set<Player> expectedResult = Set.of(player);

            reader.reset();

            footballPlayerAnalyzer = new FootballPlayerAnalyzer(reader);

            Set<Player> actualResult = footballPlayerAnalyzer.getPlayersByFullNameKeyword(player.fullName());

            assertEquals(expectedResult, actualResult);

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
