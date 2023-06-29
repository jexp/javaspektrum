package example;

import net.bytebuddy.*;
import net.bytebuddy.implementation.*;
import net.bytebuddy.matcher.*;

import net.bytebuddy.pool.*;
import net.bytebuddy.dynamic.*;
import java.lang.reflect.*;

public class PoolTest {
    /*
    TODO doesn't work, fails with:
    Class already loaded: class example.Bar
    figure out why
    */
    public static void main(String...args) throws Exception {
        TypePool typePool = TypePool.Default.ofSystemLoader();
        // typePool.describe("Bar").resolve();
        Class type = new ByteBuddy()
            .redefine(typePool.describe("example.ClassWithoutField").resolve(),
                      ClassFileLocator.ForClassLoader.ofSystemLoader())
            .defineField("qux", String.class)
            .make()
            .load(ClassLoader.getSystemClassLoader())
            .getLoaded();
        Field field = type.getDeclaredField("qux");
        System.out.println(field.getName());
    }
}
