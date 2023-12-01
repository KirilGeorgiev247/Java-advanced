package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MarkdownTablePrinter implements TablePrinter {

    private final int minWidth = 3;
    private final List<Integer> columnWidths;

    public MarkdownTablePrinter() {
        columnWidths = new ArrayList<>();
    }

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
            currWidth = Math.max(currWidth, minWidth);
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

        return " " + firstChar +
            "-".repeat(Math.max(0, width - 2)) +
            lastChar +
            " |";
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

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        calculateWidths(table);

        List<String> result = new LinkedList<>();

        result.add(getFormattedRow(table.getColumnNames()));

        result.add(getFormattedAlignments(alignments));

        for (int i = 0; i < table.getRowsCount() - 1; i++) {
            List<String> rowData = new ArrayList<>();

            for (String name : table.getColumnNames()) {
                List<String> columnData = List.copyOf(table.getColumnData(name));
                rowData.add(columnData.get(i));
            }

            result.add(getFormattedRow(rowData));
        }

        return Collections.unmodifiableCollection(result);
    }
}
