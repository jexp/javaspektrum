import java.util.function.*;

public class TestGuard {
    public static void main(String...args) {
        // System.out.println(test("Ukraine"));
        Function<Object,Integer> fun = (v) -> { if (!(v instanceof String str)) return 0; return str.length(); };
        fun.apply("Ukraine");
    }
    public static int test(Object value) {
        if (!(value instanceof String str)) return 0;
        return str.length();
    }
}