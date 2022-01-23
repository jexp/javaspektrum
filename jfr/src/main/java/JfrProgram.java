import java.util.Random;
import java.util.concurrent.TimeUnit;

public class JfrProgram {

    public static void main(String[] args) {
        int bound = 100_000;
        int seconds = 120;
        long runtime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
        Random r = new Random();
        Long result = 0L;
        while (System.currentTimeMillis() < runtime) {
            result += compute(r.nextInt(), r.nextInt(bound));
        }
        System.out.println("result = " + result+" runtime "+runtime);
    }

    // let's be a bit wasteful
    public static Long compute(int start, int count) {
        int[] data = new int[count];
        for (int i=0;i<count;i++) {
            data[i]=start + i;
        }
        Long result = 1L;
        for (int i=0;i<count;i++) {
            result *= data[i];
        }
        return result;
    }
}
