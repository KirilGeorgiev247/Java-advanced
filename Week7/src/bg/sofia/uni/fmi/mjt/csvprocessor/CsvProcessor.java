package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Scanner;

public class CsvProcessor implements CsvProcessorAPI {

    private final Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {

        try (Scanner sc = new Scanner(reader)) {
            String line;

            while (sc.hasNextLine()) {
                line = sc.nextLine();
                String[] data = line.split("\\Q" + delimiter + "\\E");
                table.addData(data);
            }
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        MarkdownTablePrinter tablePrinter = new MarkdownTablePrinter();

        Collection<String> rows = tablePrinter.printTable(table, alignments);

        try (var bufferWriter = new BufferedWriter(writer)) {
            var iterator = rows.iterator();

            while (iterator.hasNext()) {
                bufferWriter.write(iterator.next());
                bufferWriter.flush();
                if (iterator.hasNext()) {
                    bufferWriter.newLine();
                    bufferWriter.flush();
                }
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
