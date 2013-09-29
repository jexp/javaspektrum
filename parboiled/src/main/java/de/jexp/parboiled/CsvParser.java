package de.jexp.parboiled;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.support.Characters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Hunger @since 28.09.13
 */
//@BuildParseTree
public class CsvParser extends BaseParser<List<String>> {

    public static final char QUOTE = '"';
    public static final char DELIM = ',';
    public static final char NEWLINE = '\n';

    public Rule field() {
        return FirstOf(
                Sequence(QUOTE, value(), QUOTE),
                value());
    }
    public Rule value() {
        return Sequence(OneOrMore(NoneOf(new char[]{DELIM, NEWLINE, QUOTE})), ACTION(peek().add(match()) || true));
    }
    public Rule value2() {
        return Sequence(OneOrMore(NoneOf(new char[]{DELIM, NEWLINE, QUOTE})), new Action() {
            @Override
            public boolean run(Context context) {
                List<String> row = getContext().getValueStack().peek();
                row.add(match());
                return true;
            }
        });
    }

    public Rule value3() {
        return Sequence(OneOrMore(NoneOf(new char[]{DELIM, NEWLINE, QUOTE})), new AddFieldToRowAction());
    }

    public Rule line() {
        return Sequence(
                // Action, push row to stack
                push(new ArrayList<String>()),
                Sequence(field(), ZeroOrMore(DELIM, field())));
    }

    public Rule file() {
        return Sequence(
                line(),
                ZeroOrMore(NEWLINE, line()),
                Optional(NEWLINE),
                EOI);
    }

    private class AddFieldToRowAction implements Action {
        @Override
        public boolean run(Context context) {
            List<String> row = getContext().getValueStack().peek();
            row.add(match());
            return true;
        }
    }
}
