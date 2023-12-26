import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {


        List<Test> missions = Arrays.asList(
            new Test("CompanyA", "Location1"),
            new Test("CompanyA", "Location2"),
            new Test("CompanyA", "Location1"),
            new Test("CompanyB", "Location1"),
            new Test("CompanyB", "Location2"),
            new Test("CompanyB", "Location2"),
            new Test("CompanyC", "Location1"),
            new Test("D", "a"),
            new Test("D", "a"),
            new Test("D", "a"),
            new Test("D", "a"),
            new Test("D", "a"),
            new Test("E", "a"),
            new Test("E", "a"),
            new Test("E", "a"),
            new Test("E", "a"),
            new Test("E", "a"),
            new Test("D", "b"),
            new Test("D", "b"),
            new Test("D", "b"),
            new Test("D", "b"),
            new Test("D", "b"),
            new Test("D", "b")
        );

        var test = missions.stream()
            .map(m -> new AbstractMap.SimpleEntry<>(m.company(), m.location()))
            .collect(Collectors.groupingBy(
                e -> Arrays.asList(e.getKey(), e.getValue()),
                Collectors.counting()
            ));

        var test2 = test
        .entrySet().stream()
            .collect(Collectors.groupingBy(
                e -> e.getKey().getFirst(),
                Collectors.collectingAndThen(
                    Collectors.maxBy(Map.Entry.comparingByValue()),
                    e -> e.map(compLocTuple -> compLocTuple.getKey().getLast()).orElse(null)
                )
            ));

        var test3 = 5;
    }

    record Test(String company, String location) {}
}