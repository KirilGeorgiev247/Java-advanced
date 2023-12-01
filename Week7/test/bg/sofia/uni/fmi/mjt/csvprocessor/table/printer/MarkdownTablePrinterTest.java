package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;


public class MarkdownTablePrinterTest {

    private final List<Integer> columnWidths = new ArrayList<>();
    MarkdownTablePrinter tablePrinter;
    Table table;
    String[] columnNames = new String[] {"first", "second", "third", "fourth"};
    ColumnAlignment[] validAlignments =
        new ColumnAlignment[] {ColumnAlignment.LEFT, ColumnAlignment.LEFT, ColumnAlignment.RIGHT,
            ColumnAlignment.CENTER};
    ColumnAlignment[] moreAlignments =
        new ColumnAlignment[] {ColumnAlignment.LEFT, ColumnAlignment.LEFT, ColumnAlignment.RIGHT,
            ColumnAlignment.CENTER, ColumnAlignment.LEFT, ColumnAlignment.LEFT, ColumnAlignment.LEFT};
    ColumnAlignment[] lessAlignments =
        new ColumnAlignment[] {ColumnAlignment.LEFT};
    String[] firstRow = new String[] {"very long long", "basic", "basic 2", "basic 3"};
    String[] secondRow = new String[] {"basic", "basic2", "basic3", "basic4"};

    private Integer getBiggestWidth(Collection<String> data, String columnName) {
        int max = 0;

        for (String curr : data) {
            max = Math.max(max, curr.length());
        }

        max = Math.max(max, columnName.length());

        return max;
    }

    private void calculateWidths(Table table) {
        Collection<String> names = table.getColumnNames();

        for (String name : names) {
            int currWidth = getBiggestWidth(table.getColumnData(name), name);
            currWidth = Math.max(currWidth, 3);
            columnWidths.add(currWidth);
        }
    }

    private String getFormattedValue(String value, int width) {
        StringBuilder result = new StringBuilder(" ");

        result.append(value);

        int diff = width - value.length();

        result.append(" ".repeat(Math.max(0, diff)));

        result.append(" |");

        return result.toString();
    }

    private String getFormattedRow(Collection<String> values) {
        StringBuilder result = new StringBuilder("|");
        int ind = 0;

        for (String value : values) {
            result.append(getFormattedValue(value, columnWidths.get(ind++)));
        }

        return result.toString();
    }

    private String getFormattedAlignment(ColumnAlignment alignment, int width) {

        char firstChar = switch (alignment) {

            case LEFT, CENTER -> ':';
            case RIGHT, NOALIGNMENT -> '-';
        };

        char lastChar = switch (alignment) {

            case RIGHT, CENTER -> ':';
            case LEFT, NOALIGNMENT -> '-';
        };

        return " " + firstChar + "-".repeat(Math.max(0, width - 2)) + lastChar + " |";
    }

    private String getFormattedAlignments(ColumnAlignment[] columnAlignments) {
        int columnsCount = columnWidths.size();
        int alignmentsCount = 0;
        int ind = 0;

        if (columnAlignments != null) {
            alignmentsCount = Math.min(columnAlignments.length, columnsCount);
        }

        StringBuilder result = new StringBuilder("|");

        for (int i = 0; i < alignmentsCount; i++) {
            result.append(getFormattedAlignment(columnAlignments[i], columnWidths.get(ind++)));
        }

        for (int i = alignmentsCount; i < columnsCount; i++) {
            result.append(getFormattedAlignment(ColumnAlignment.NOALIGNMENT, columnWidths.get(ind++)));
        }

        return result.toString();
    }

    @Test
    void testIfValidTableIsCorrectlyPrinted() throws CsvDataNotCorrectException {
        tablePrinter = new MarkdownTablePrinter();
        table = new BaseTable();

        table.addData(columnNames);
        table.addData(firstRow);
        table.addData(secondRow);

        Collection<String> actual = tablePrinter.printTable(table, validAlignments);

        List<String> expected = new ArrayList<>();

        calculateWidths(table);

        expected.add(getFormattedRow(table.getColumnNames()));
        expected.add(getFormattedAlignments(validAlignments));
        expected.add(getFormattedRow(List.of(firstRow)));
        expected.add(getFormattedRow(List.of(secondRow)));

        assertIterableEquals(expected, actual, "Table should be printed in the right format!");
    }

    @Test
    void testIfTableIsCorrectlyPrintedWhenTooManyAlignments() throws CsvDataNotCorrectException {
        tablePrinter = new MarkdownTablePrinter();
        table = new BaseTable();

        table.addData(columnNames);
        table.addData(firstRow);
        table.addData(secondRow);

        Collection<String> actual = tablePrinter.printTable(table, moreAlignments);

        List<String> expected = new ArrayList<>();

        calculateWidths(table);

        expected.add(getFormattedRow(table.getColumnNames()));
        expected.add(getFormattedAlignments(moreAlignments));
        expected.add(getFormattedRow(List.of(firstRow)));
        expected.add(getFormattedRow(List.of(secondRow)));

        assertIterableEquals(expected, actual,
            "Table should be printed in the right format even if alignments are more than needed!");
    }

    @Test
    void testIfTableIsCorrectlyPrintedWhenLessAlignments() throws CsvDataNotCorrectException {
        tablePrinter = new MarkdownTablePrinter();
        table = new BaseTable();

        table.addData(columnNames);
        table.addData(firstRow);
        table.addData(secondRow);

        Collection<String> actual = tablePrinter.printTable(table, lessAlignments);

        List<String> expected = new ArrayList<>();

        calculateWidths(table);

        expected.add(getFormattedRow(table.getColumnNames()));
        expected.add(getFormattedAlignments(lessAlignments));
        expected.add(getFormattedRow(List.of(firstRow)));
        expected.add(getFormattedRow(List.of(secondRow)));

        assertIterableEquals(expected, actual,
            "Table should be printed in the right format even if alignments are less than needed!");
    }
}
