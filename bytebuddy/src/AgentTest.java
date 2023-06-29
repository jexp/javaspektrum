import net.bytebuddy.*;
import net.bytebuddy.agent.*;
import net.bytebuddy.dynamic.loading.*;

class Foo {
  String m() { return "foo"; }
}
 
class Bar {
  String m() { return "bar"; }
}

public class AgentTest {

    public static void main(String[] args) {
        ByteBuddyAgent.install();
        Foo foo = new Foo();
        Bar bar = new Bar();
        new ByteBuddy()
        .redefine(Bar.class)
        .name(Foo.class.getName())
        .make()
        .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        System.out.println(foo.m());
        System.out.println(bar.m());
    }
}
