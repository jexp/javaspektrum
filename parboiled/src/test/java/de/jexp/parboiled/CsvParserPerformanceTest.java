package de.jexp.parboiled;

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.StringReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Michael Hunger @since 29.09.13
 */
public class CsvParserPerformanceTest {

    // note, build parse tree makes parboiled slow and takes exponential amount of ram and time

    public static final CsvParser PARSER = Parboiled.createParser(CsvParser.class);
    public static final int ROWS = 1000000;
    public static final int COLUMNS = 30;
    public static final char DELIM = ',';
    public static final String input = generateInput(ROWS, COLUMNS, DELIM);

    @Test
    public void testParseWithParboiled() throws Exception {
        BasicParseRunner<Object> runner = new BasicParseRunner<Object>(PARSER.file());
        char[] chars = input.toCharArray();
        long time;
        for (int i = 0; i < 10; i++) {
            time = currentTimeMillis();
            ParsingResult<?> result = runner.run(chars);
            long delta = currentTimeMillis() - time;
            out.println("Input size " + input.length() + " errors " + result.hasErrors() + " took " + delta + " ms " + ROWS / delta + " rows per ms");
        }
    }
    @Test
    public void testParseStreamingWithParboiled() throws Exception {
        CountingConsumer countingConsumer = new CountingConsumer();
        StreamingCsvParser parser = Parboiled.createParser(StreamingCsvParser.class, countingConsumer);
        BasicParseRunner<Object> runner = new BasicParseRunner<Object>(parser.file());
        char[] chars = input.toCharArray();
        long time;
        for (int i = 0; i < 10; i++) {
            time = currentTimeMillis();
            ParsingResult<?> result = runner.run(chars);
            long delta = currentTimeMillis() - time;
            out.println("Input size " + input.length() + " errors " + result.hasErrors() +" counted " +countingConsumer.getCount()+" took " + delta + " ms " + ROWS / delta + " rows per ms");
        }
    }

    @Test
    public void testParseWithOpenCSV() throws Exception {
        long time;
        for (int i = 0; i < 3; i++) {
            time = currentTimeMillis();
            CSVReader reader = new CSVReader(new StringReader(input), DELIM);
            int rows = 0;
            while (reader.readNext() != null) {
                rows++;
            }
            reader.close();
            long delta = currentTimeMillis() - time;
            out.println("Input size " + input.length() + " rows " + rows + " took " + delta + " ms " + ROWS / delta + " rows per ms");
        }
    }

    private static String generateInput(int rows, int columns, char delim) {
        long time = currentTimeMillis();
        out.println("start generate input");
        StringBuilder sb = new StringBuilder(columns * 10 * rows);
        for (int row = 0; row < rows; row++) {
            if (row != 0) sb.append("\n");
            for (int col = 0; col < columns; col++) {
                if (col != 0) sb.append(delim);
                sb.append("asdfkjl");
            }
        }
        out.println("took = " + (currentTimeMillis() - time));
        return sb.toString();
    }

    private static class CountingConsumer implements StreamingCsvParser.Consumer<List<String>> {
        int count=0;

        @Override
        public boolean accept(List<String> value) {
            count++;
            return true;
        }

        public int getCount() {
            return count;
        }
    }
}
