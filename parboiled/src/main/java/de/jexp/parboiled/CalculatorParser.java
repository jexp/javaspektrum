package de.jexp.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

/**
 * @author Michael Hunger @since 28.09.13
 */
@BuildParseTree
class CalculatorParser extends BaseParser<Object> {

    Rule Expression() {
        return Sequence(Term(), ZeroOrMore(AnyOf("+-"), Term()));
    }

    Rule Term() {
        return Sequence(Factor(), ZeroOrMore(AnyOf("*/"), Factor()));
    }

    Rule Factor() {
        return FirstOf(Number(), Sequence('(', Expression(), ')'));
    }

    Rule Number() {
        return OneOrMore(CharRange('0', '9'));
    }
}