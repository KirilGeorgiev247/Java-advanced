package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvProcessorTest {

    CsvProcessorAPI csvProcessor = new CsvProcessor();

    String validInput = "hdr,testheader,z" + System.lineSeparator() + "testcolumn,b,c";

    String dotInput = "hdr.testheader.z" + System.lineSeparator() + "testcolumn.b.c";

    String invalidInput = "edno, dve, tri" + System.lineSeparator() + "nyama, chetiri";

    String expected =
        "| hdr        | testheader | z   |" + System.lineSeparator() + "| ---------: | :--------- | :-: |" +
            System.lineSeparator() + "| testcolumn | b          | c   |";

    ColumnAlignment[] alignments =
        new ColumnAlignment[] {ColumnAlignment.RIGHT, ColumnAlignment.LEFT, ColumnAlignment.CENTER};

    @Test
    void testIfCsvReaderAndWriterWorkCorrectly() throws CsvDataNotCorrectException {
        Reader reader = new StringReader(validInput);

        csvProcessor.readCsv(reader, ",");

        Writer writer = new StringWriter();

        csvProcessor.writeTable(writer, alignments);

        assertEquals(expected, writer.toString(), "Csv should be read and written in the right format successfully!");
    }

    @Test
    void testIfCsvReaderThrowsWhenInvalidInput() {
        Reader reader = new StringReader(invalidInput);

        assertThrows(CsvDataNotCorrectException.class, () -> csvProcessor.readCsv(reader, ","),
            "Exception should be thrown when data is incorrect!");
    }

    @Test
    void testIfNoAlignmentsDataReturnsCorrectly() throws CsvDataNotCorrectException {
        Reader reader = new StringReader(validInput);

        csvProcessor.readCsv(reader, ",");

        Writer writer = new StringWriter();

        csvProcessor.writeTable(writer);

        String defExpected =
            "| hdr        | testheader | z   |" + System.lineSeparator() + "| ---------- | ---------- | --- |" +
                System.lineSeparator() + "| testcolumn | b          | c   |";

        assertEquals(defExpected, writer.toString(), "Csv should be read and written in the right format successfully!");
    }

    @Test
    void testIfDotSeparatedDataReturnsCorrectly() throws CsvDataNotCorrectException {
        Reader reader = new StringReader(dotInput);

        csvProcessor.readCsv(reader, ".");

        Writer writer = new StringWriter();

        csvProcessor.writeTable(writer);

        String defExpected =
            "| hdr        | testheader | z   |" + System.lineSeparator() + "| ---------- | ---------- | --- |" +
                System.lineSeparator() + "| testcolumn | b          | c   |";

        assertEquals(defExpected, writer.toString(), "Csv should be read and written in the right format successfully!");

    }
}
