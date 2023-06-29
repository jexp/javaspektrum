// jar -c -v -f laufzeit.jar -m ../laufzeit.mf example/LaufzeitAgent*
// java -javaagent:laufzeit.jar -cp ../byte-buddy-1.12.10.jar:. example.ByteBuddyTest
package example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class LaufzeitAgent {
    static class LaufzeitAdvice {
        @Advice.OnMethodEnter
        static long vorher(@Advice.Origin String methode,
                           @Advice.Local("zeit") long zeit) {
            System.err.println("Vorher: " + methode);
            zeit = System.nanoTime();
            return zeit;
        }

        @Advice.OnMethodExit
        static void nachher(@Advice.Origin String methode,
                            @Advice.Enter long startZeit,
                            @Advice.Return Object ergebnis,
                            @Advice.Local("zeit") long zeit) {
            long jetzt = System.nanoTime();
            System.err.println("Nachher: " + (jetzt-zeit));
            System.err.println("Zeitmessung " + methode + " Ergebnis "+ergebnis+"  benötigte " + (jetzt - startZeit) + " ns.");
        }
    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        System.err.println("Running premain");
        Advice advice = Advice.to(LaufzeitAdvice.class);
        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("example."))
                .transform((builder, type, cl, modul) -> builder
                        // .method(ElementMatchers.nameContainsIgnoreCase("ermittle")).intercept(advice))
                        .visit(advice.on(ElementMatchers.nameContainsIgnoreCase("ermittle"))))
                .installOn(instrumentation);
    }
}
