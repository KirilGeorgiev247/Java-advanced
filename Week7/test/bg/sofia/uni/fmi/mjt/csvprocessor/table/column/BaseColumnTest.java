package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;


import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseColumnTest {

    private Column column;

    @Test
    void testIfColumnDataIsCreatedSuccessfullyWhenCorrect() {
        Set<String> data = Set.of("abc", "def", "fef");

        column = new BaseColumn(data);

        assertIterableEquals(column.getData(), data,
            "Data should be the same as provided");
    }

    @Test
    void testIfColumnDataIsCreatedSuccessfullyWhenEmpty() {
        column = new BaseColumn();

        assertNotNull(column.getData(), "Data should initialize itself when no collection is provided!");

        assertTrue(column.getData().isEmpty(), "Data should be empty when no collection is provided!");
    }

    @Test
    void testIfColumnDataIsCreatedSuccessfullyWhenNull() {
        column = new BaseColumn(null);

        assertNotNull(column.getData(), "Data should initialize itself when provided collection is null!");

        assertTrue(column.getData().isEmpty(), "Data should be empty when provided collection is null!");
    }

    @Test
    void testIfCreationThrowsWhenProvidedDataIsIncorrect() {
        Set<String> data = Set.of("abc", "", "fef");

        assertThrows(IllegalArgumentException.class, () -> new BaseColumn(data),
            "Constructor should throw when provided data is incorrect!");
    }

    @Test
    void testIfDataIsSuccessfullyAdded() {
        Set<String> data = Set.of("first", "second", "third");
        String toBeAdded = "fourth";

        column = new BaseColumn(data);

        column.addData(toBeAdded);

        assertTrue(column.getData().contains(toBeAdded), "Added data should be contained in the collection!");
    }

    @Test
    void testIfDataSetterThrowsWhenDataIsInvalid() {
        Set<String> data = Set.of("first", "second", "third");

        column = new BaseColumn(data);

        assertThrows(IllegalArgumentException.class, () -> column.addData(null),
            "Null data cannot be added!");

        assertThrows(IllegalArgumentException.class, () -> column.addData(""),
            "Empty data cannot be added!");

        assertThrows(IllegalArgumentException.class, () -> column.addData("   "),
            "Blank data cannot be added!");
    }
}
