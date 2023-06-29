package example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

public class RetransformAgent {
    public static void agentmain(String argument,
                                 Instrumentation instrumentation) {
        Advice advice = Advice.to(LaufzeitAgent.LaufzeitAdvice.class);
        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .with(AgentBuilder.TypeStrategy.Default.DECORATE)
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE_FROZEN)
                .disableClassFormatChanges()
                .type(ElementMatchers.any())
                .transform((DynamicType.Builder<?> builder,
                            TypeDescription type,
                            ClassLoader loader,
                            JavaModule module) -> {
                    return builder.visit(advice.on(ElementMatchers.any()));
                }).installOn(instrumentation);
    }
}
/*
 builder.visit(advice.on(ElementMatchers.any()))
 builder.method(ElementMatchers.any()).intercept(advice))
 */
