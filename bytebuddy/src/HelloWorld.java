import net.bytebuddy.*;
import net.bytebuddy.implementation.*;
import net.bytebuddy.matcher.*;

import java.util.Objects;

public class HelloWorld {
    public static void main(String[] args) throws Exception {
        Class<?> hello = new ByteBuddy() // <1>
                .subclass(Object.class) // <2>
                .method(ElementMatchers.named("toString")) // <3>
                .intercept(FixedValue.value("Hallo JavaSpektrum!")) // <4>
                .make()
                .load(ClassLoader.getSystemClassLoader()) // <5>
                .getLoaded();

        var toString = hello.getDeclaredConstructor().newInstance().toString();
        System.out.println("toString = " + toString);
        assert Objects.equals(toString, "Hallo JavaSpektrum!");
    }
}
