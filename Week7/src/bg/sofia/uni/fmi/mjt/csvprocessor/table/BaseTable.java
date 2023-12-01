package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseTable implements Table {

    private Map<String, Column> data;

    private List<String> columnNames;

    public BaseTable() {
        data = new HashMap<>();
        columnNames = new ArrayList<>();
    }

    private void addColumns(String[] data) throws CsvDataNotCorrectException {
        for (String str :
            data) {
            columnNames.add(str);
            if (this.data.containsKey(str)) {
                throw new CsvDataNotCorrectException("Column names cannot be the same!");
            }
            this.data.put(str, new BaseColumn());
        }
    }

    private void addRow(String[] data) {
        for (int i = 0; i < data.length; i++) {
            String columnName = columnNames.get(i);
            String currData = data[i];
            this.data.get(columnName).addData(currData);
        }
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        for (String str :
            data) {
            if (str == null || str.isBlank() || str.isEmpty()) {
                throw new IllegalArgumentException("Data content cannot be null");
            }
        }

        if (columnNames.isEmpty()) {
            addColumns(data);
        } else {
            if (columnNames.size() != data.length) {
                throw new CsvDataNotCorrectException("Data is in incorrect format");
            }
            addRow(data);
        }
    }

    @Override
    public Collection<String> getColumnNames() {
        return Collections.unmodifiableCollection(columnNames);
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null || column.isBlank() || column.isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty!");
        }

        if (!columnNames.contains(column)) {
            throw new IllegalArgumentException("Such column does not exist!");
        }

        return Collections.unmodifiableCollection(data.get(column).getData());
    }

    @Override
    public int getRowsCount() {
        return columnNames.isEmpty() ? 0 : data.get(columnNames.get(0)).getData().size() + 1;
    }
}
