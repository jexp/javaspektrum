import example.ToString;

@ToString
public class ToStringTest {
  @ToString static class Bar { }
  public static void main(String...args) {
     System.out.println(new ToStringTest());
     System.out.println(new Bar());
  }
}

