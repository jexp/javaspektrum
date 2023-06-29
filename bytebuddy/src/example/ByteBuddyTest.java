package example;

public class ByteBuddyTest {
    public static void main(String[] args) {
        var result = ermittleWert();
        System.out.println("result = " + result);
    }

    public static double ermittleWert() {
        double sum = 0;
        for (int i = 0; i<100000; i++) {
          sum += Math.sin(i);
        }
        return sum;
    }
}
