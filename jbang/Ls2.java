import java.nio.file.*;

public class Ls2 {
    public static void main(String...args) throws Exception {
        try (var stream = Files.walk(Paths.get(args[0]), /*depth*/ 3)) {
            stream
              .filter(file -> !Files.isDirectory(file))
              .map(Path::getFileName)
              .map(Path::toString)
              .forEach(System.out::println);
        }
    }
}
