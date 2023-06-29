package example;

import net.bytebuddy.implementation.*;
import net.bytebuddy.agent.builder.*;
import net.bytebuddy.description.type.*;
import net.bytebuddy.dynamic.*;
import net.bytebuddy.utility.*;
import static net.bytebuddy.matcher.ElementMatchers.*;

import java.lang.instrument.*;

public class ToStringAgent {
  public static void premain(String arguments, Instrumentation instrumentation) {
    System.err.println("example.ToStringAgent Test");
    new AgentBuilder.Default()
        .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
        .type(isAnnotatedWith(ToString.class))
        .transform(new AgentBuilder.Transformer() {
            public DynamicType.Builder transform(DynamicType.Builder builder,
                                              TypeDescription typeDescription,
                                              ClassLoader classloader, 
                                              JavaModule module) {
                return builder.method(named("toString"))
                          .intercept(FixedValue.value("transformed"));
            }
        }).installOn(instrumentation);
  }
}

