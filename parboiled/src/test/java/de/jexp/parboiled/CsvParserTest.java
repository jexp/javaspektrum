package de.jexp.parboiled;

import org.junit.Ignore;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;

/**
 * @author Michael Hunger @since 29.09.13
 */
public class CsvParserTest {


    public static final CsvParser PARSER = Parboiled.createParser(CsvParser.class);

    @Test
    @Ignore
    public void testParseField() throws Exception {
        ReportingParseRunner<?> runner = new ReportingParseRunner<Object>(PARSER.field());
        assertEquals(EMPTY_LIST, parse(runner,"a"));
        assertEquals(EMPTY_LIST, parse(runner,"\"a\""));
    }
    @Test
    public void testParseInvalidQuotedField() throws Exception {
        ReportingParseRunner<?> runner = new ReportingParseRunner<Object>(PARSER.file());
        assertEquals(1, parse(runner,"\"a\"a\"").size());
    }

    @Test
    public void testParseLine() throws Exception {
        ReportingParseRunner<?> runner = new ReportingParseRunner<Object>(PARSER.line());
        assertEquals(EMPTY_LIST, parse(runner, "a,\"b\",c"));
        assertEquals(EMPTY_LIST, parse(runner, "a"));
    }
    @Test
    public void testParseLineInFile() throws Exception {
        ReportingParseRunner<?> runner = new ReportingParseRunner<Object>(PARSER.file());
        assertEquals(EMPTY_LIST, parse(runner, "a,b,c"));
        assertEquals(EMPTY_LIST, parse(runner, "a"));
    }
    @Test
    public void testParseFile() throws Exception {
        ReportingParseRunner<?> runner = new ReportingParseRunner<Object>(PARSER.file());
        assertEquals(EMPTY_LIST, parse(runner, "a,b,c\na,b,c"));
        assertEquals(EMPTY_LIST, parse(runner, "a,b,c\na,b,c\n"));
    }

    @Test
    public void testParseInvalidFile() throws Exception {
        ReportingParseRunner<?> runner = new ReportingParseRunner<Object>(PARSER.file());
        assertEquals(1, parse(runner, "a,").size());
    }

    private List<String> parse(ReportingParseRunner<?> runner, String input) {
        ParsingResult<?> result = runner.run(input);
        System.out.println("Value Stack for input '"+input+"'");
        for (Object element : result.valueStack) {
            System.out.println(element);
        }
        if (!result.hasErrors()) return EMPTY_LIST;

        System.out.println("input = " + input);
        System.out.println(ParseTreeUtils.printNodeTree(result));
        System.out.println("result.hasErrors() = " + result.hasErrors());

        List<String> errors=new ArrayList<>(result.parseErrors.size());
        for (ParseError parseError : result.parseErrors) {
            errors.add(parseError.getErrorMessage());
        }
        return errors;
    }
}
