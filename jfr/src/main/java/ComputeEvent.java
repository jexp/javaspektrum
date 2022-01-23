import jdk.jfr.*;

@Name(ComputeEvent.NAME) // <1>
@Label("Compute Event")
@Category("JavaSpektrum")
@Description("Simple Demonstration Compute Event")
@StackTrace(false)  // <2>
public class ComputeEvent extends jdk.jfr.Event {
    static final String NAME = "javaspektrum.ComputeEvent";

    @Label("Count") // <3>
    @Unsigned // <4>
    long count;
    @Label("Memory")
    @DataAmount
    long bytes;
    @Label("Frequency")
    @Frequency
    double operationsPerSecond;
}
