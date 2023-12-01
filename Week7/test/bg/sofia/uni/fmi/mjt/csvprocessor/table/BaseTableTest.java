package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseTableTest {

    private Table table;

    @Test
    void testIfAddDataThrowsIfDataIsNullOrEmpty() {
        table = new BaseTable();

        String[] emptyArr = new String[] {};

        assertThrows(IllegalArgumentException.class, () -> table.addData(null),
            "Add data should throw when data is null!");

        assertThrows(IllegalArgumentException.class, () -> table.addData(emptyArr),
            "Add data should throw when data is empty!");
    }

    @Test
    void testIfAddDataThrowsWhenDataContentIsNullOrEmpty() {
        table = new BaseTable();

        assertThrows(IllegalArgumentException.class, () -> table.addData(new String[] {""}),
            "Add data should throw when data content is empty!");

        assertThrows(IllegalArgumentException.class, () -> table.addData(new String[] {null}),
            "Add data should throw when data content is null!");

        assertThrows(IllegalArgumentException.class, () -> table.addData(new String[] {"  "}),
            "Add data should throw when data content is blank!");
    }

    @Test
    void testIfColumnNamesAreAddedSuccessfully() throws CsvDataNotCorrectException {
        table = new BaseTable();

        String[] columnNames = new String[] {"first", "second", "third"};

        table.addData(columnNames);

        assertIterableEquals(table.getColumnNames(), Arrays.asList(columnNames),
            "Column names should be added successfully when no currently names exist!");
    }

    @Test
    void testIfAddDataThrowsWhenColumnNamesCollide() {
        table = new BaseTable();

        String[] columnNames = new String[] {"first", "second", "first"};

        assertThrows(CsvDataNotCorrectException.class, () -> table.addData(columnNames),
            "Add data should throw when two column names have equal names!");
    }

    @Test
    void testIfAddDataThrowsWhenDataHasMoreOrLessColumnsThanExpected() throws CsvDataNotCorrectException {
        table = new BaseTable();

        String[] columnNames = new String[] {"first", "second", "third"};

        String[] longerData = Arrays.copyOf(columnNames, columnNames.length + 1);
        longerData[longerData.length - 1] = "fourth";

        table.addData(columnNames);

        assertThrows(CsvDataNotCorrectException.class,
            () -> table.addData(Arrays.copyOfRange(columnNames, 0, columnNames.length - 1)),
            "Add data should throw when data columns are less than expected!");

        assertThrows(CsvDataNotCorrectException.class,
            () -> table.addData(longerData),
            "Add data should throw when data columns are more than expected!");
    }

    @Test
    void testIfRowIsAddedSuccessfully() throws CsvDataNotCorrectException {
        table = new BaseTable();

        String[] columnNames = new String[] {"first", "second", "third"};
        String[] data1 = new String[] {"1", "2", "3"};
        String[] data2 = new String[] {"3", "2", "1"};

        table.addData(columnNames);
        table.addData(data1);
        table.addData(data2);

        Collection<String> expected = List.of("1", "3");

        assertIterableEquals(expected, table.getColumnData(columnNames[0]),
            "Data should be equal to added!");
    }

    @Test
    void testIfGetColumnDataThrowsWhenNoSuchColumn() throws CsvDataNotCorrectException {
        table = new BaseTable();

        String[] columnNames = new String[] {"first", "second", "third"};
        String[] data1 = new String[] {"1", "2", "3"};
        String[] data2 = new String[] {"3", "2", "1"};

        table.addData(columnNames);
        table.addData(data1);
        table.addData(data2);

        assertThrows(IllegalArgumentException.class, () -> table.getColumnData("random"),
            "Get column data should throw when no such column exists!");
    }

    @Test
    void testIfGetColumnDataThrowsWhenColumnNameIsInvalid() throws CsvDataNotCorrectException {
        table = new BaseTable();

        String[] columnNames = new String[] {"first", "second", "third"};
        String[] data1 = new String[] {"1", "2", "3"};
        String[] data2 = new String[] {"3", "2", "1"};

        table.addData(columnNames);
        table.addData(data1);
        table.addData(data2);

        assertThrows(IllegalArgumentException.class, () -> table.getColumnData("  "),
            "Get column data should throw when no column name is blank!");

        assertThrows(IllegalArgumentException.class, () -> table.getColumnData(null),
            "Get column data should throw when no column name is null!");

        assertThrows(IllegalArgumentException.class, () -> table.getColumnData(""),
            "Get column data should throw when no column name is empty!");
    }

    @Test
    void testIfRowsAreEqualToAddedData() throws CsvDataNotCorrectException {
        table = new BaseTable();

        String[] columnNames = new String[] {"first", "second", "third"};
        table.addData(columnNames);

        String[] data1 = new String[] {"1", "2", "3"};
        String[] data2 = new String[] {"3", "2", "1"};

        List<String[]> datas = List.of(data1, data2);

        for (String[] data :
            datas) {
            table.addData(data);
        }

        assertEquals(datas.size() + 1, table.getRowsCount(),
            "Rows count should be equal to added data count!");
    }

}
