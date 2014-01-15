package de.jexp.parboiled;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Hunger @since 28.09.13
 */
//@BuildParseTree
public class CsvParserWithoutActions extends BaseParser<List<String>> {

    public static final char QUOTE = '"';
    public static final char DELIM = ',';
    public static final char NEWLINE = '\n';

    public Rule field() {
        return FirstOf(
                Sequence(QUOTE, value(), QUOTE),
                value());
    }
    public Rule value() {
        return OneOrMore(NoneOf(new char[]{DELIM, NEWLINE, QUOTE}));
    }

    public Rule line() {
        return Sequence(field(), ZeroOrMore(DELIM, field()));
    }

    public Rule file() {
        return Sequence(
                line(),
                ZeroOrMore(NEWLINE, line()),
                Optional(NEWLINE),
                EOI);
    }
}
