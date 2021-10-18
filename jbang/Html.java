///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.14.3
//JAVA 17

// jbang --quiet Html.java "https://en.wikipedia.org/" "#mp-itn b a"
import static java.lang.System.*;
import org.jsoup.*;

public class Html {
    public static void main(String...args) throws Exception {
        var doc = Jsoup.connect(args[0]).get();
        out.println(doc.title());
        doc.select(args[1]).stream()
        .map(e -> new Anchor(e.text(), e.attr("href")))
        .forEach(out::println);
    }
    record Anchor(String text, String href) {}
}
