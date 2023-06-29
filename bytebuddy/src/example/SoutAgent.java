package example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.utility.JavaModule;

import java.io.PrintStream;
import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class SoutAgent {
  public static void premain(String arguments, Instrumentation instrumentation) {
    System.err.println("example.SoutAgent Test");
    new AgentBuilder.Default()
        .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
            .type(hasSuperType(named("RequestHandler")))
        .transform((builder, typeDescription, classloader, module) -> {
                try {
                    return builder.method(any())
                            .intercept(MethodCall.invoke(
                                    PrintStream.class.getMethod("println", String.class))
                                    .onField(System.class.getField("out"))
                                    .with("Hello World").andThen(SuperMethodCall.INSTANCE));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        ).installOn(instrumentation);
  }
}

