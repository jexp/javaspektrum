import jdk.jfr.FlightRecorder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class JfrProgram2 {

    public static void main(String[] args) {
        FlightRecorder.register(ComputeEvent.class);

        int bound = 100_000;
        int seconds = 120;
        long runtime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
        Random r = new Random();
        Long result = 0L;
        while (System.currentTimeMillis() < runtime) {
            ComputeEvent event = new ComputeEvent();
            int count = r.nextInt(bound);
            long startTime = System.nanoTime();
            if (event.isEnabled()) {
                event.count = count;
            }
            long computed = JfrProgram.compute(r.nextInt(), count);
            result += computed;
            if (event.shouldCommit()) {
                event.bytes = count * Integer.BYTES;
                event.operationsPerSecond = 1_000_000_000d * count / (System.nanoTime()-startTime);
                event.commit();
            }
        }
        System.out.println("result = " + result+" runtime "+runtime);
    }
}
