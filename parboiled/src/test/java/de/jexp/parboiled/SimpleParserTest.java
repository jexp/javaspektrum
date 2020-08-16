package de.jexp.parboiled;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

/**
 * @author Michael Hunger @since 28.09.13
 */
public class SimpleParserTest {
    @Test
    public void testParseCalculatorExpression() throws Exception {
        String input = "1+2";
        CalculatorParser parser = Parboiled.createParser(CalculatorParser.class);
        ReportingParseRunner<Object> parseRunner = new ReportingParseRunner<>(parser.Expression());
        ParsingResult<?> result = parseRunner.run(input);
        System.out.println("Errors "+result.parseErrors);
        System.out.println("Result "+result.resultValue);
        System.out.println("Matched "+result.matched);
        System.out.println("ValueStack size "+result.valueStack.size());
        System.out.println("ParseTreeRoot "+result.parseTreeRoot);
        System.out.println(ParseTreeUtils.printNodeTree(result));
    }
}
