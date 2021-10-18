import java.io.*;

public class Ls {
    public static void main(String...args) {
        for (var f : new File(args[0]).listFiles()) 
            System.out.println(f);
    }
}
