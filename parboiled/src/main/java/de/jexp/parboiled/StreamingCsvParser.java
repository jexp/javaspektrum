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
public class StreamingCsvParser extends BaseParser<List<String>> {


    public interface Consumer<T> {
        boolean accept(T value);
    }
    private Consumer<List<String>> consumer;

    public StreamingCsvParser(Consumer<List<String>> consumer) {
        this.consumer = consumer;
    }

    public static final char QUOTE = '"';
    public static final char DELIM = ',';
    public static final char NEWLINE = '\n';

    public Rule field() {
        return FirstOf(
                Sequence(QUOTE, value(), QUOTE),
                value());
    }

    public Rule value() {
        return Sequence(OneOrMore(NoneOf(new char[]{DELIM, NEWLINE, QUOTE})), new AddFieldToRowAction());
    }

    public Rule line() {
        return Sequence(
                Sequence(field(), ZeroOrMore(DELIM, field())),
                new PublishRowAction()
                );
    }

    public Rule file() {
        return Sequence(
                push(new ArrayList<String>()),
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
    private class PublishRowAction implements Action {
        @Override
        public boolean run(Context context) {
            List<String> row = getContext().getValueStack().peek();
            boolean doContinue = consumer.accept(row);
            row.clear();
            return doContinue;
        }
    }
}
