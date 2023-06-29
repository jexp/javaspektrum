import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class DelegateStaticTest {
    public static void main(String[] args) throws Exception {
        // delegateStatic();
        delegateCall();
        // delegateGeneric();

        MethodDelegation.to(RequestProxy.class)
                .andThen(MethodCall.run(() -> {/* do something*/}));
    }

    private static void delegateStatic() throws Exception {
        var subClass = new ByteBuddy()
                .subclass(RequestHandler.class)
                .method(named("handleRequest")
                        .and(isDeclaredBy(RequestHandler.class)
                                .and(returns(int.class))))
                .intercept(MethodDelegation.to(RequestProxy.class))
                .make()
                .load(RequestHandler.class.getClassLoader())
                .getLoaded();

        var requestString = "Byte Buddy rocks!";
        int result = subClass.getDeclaredConstructor().newInstance().handleRequest(requestString);
        // result = 17

        assert RequestProxy.proxyRequest(requestString) == result;
    }

    private static void delegateCall() throws Exception {
        TypeDescription typeDescription = TypeDescription.ForLoadedType.of(RequestProxy.class);
        MethodDescription methodDescription = typeDescription.getDeclaredMethods()
                .filter(isMethod().and(takesArguments(String.class)).and(returns(int.class)))
                .getOnly();

        var subClass = new ByteBuddy()
                .subclass(RequestHandler.class)
                .method(named("handleRequest")
                        .and(isDeclaredBy(RequestHandler.class)
                                .and(returns(int.class))))
                .intercept(MethodCall.invoke(methodDescription).withArgument(0))
                .make()
                .load(RequestHandler.class.getClassLoader())
                .getLoaded();

        var requestString = "Byte Buddy rocks!";
        int result = subClass.getDeclaredConstructor().newInstance().handleRequest(requestString);
        // result = 17
        // MethodCall.invoke()
        /*MethodCall.call(() -> { System.out.println("callback");return 17; })*/

        assert RequestProxy.proxyRequest(requestString) == result;
    }
    private static void delegateGeneric() throws Exception {
        var subClass = new ByteBuddy()
                .subclass(RequestHandler.class)
                .method(named("handleRequest")
                        .and(isDeclaredBy(RequestHandler.class)
                                .and(returns(int.class))))
                .intercept(MethodDelegation.to(MyInterceptor.class))
                .make()
                .load(RequestHandler.class.getClassLoader())
                .getLoaded();

        var requestString = "Byte Buddy rocks!";
        int result = subClass.getDeclaredConstructor().newInstance().handleRequest(requestString);
        // result = 17

        assert RequestProxy.proxyRequest(requestString) == result;
    }
    public static class RequestProxy {
        public static int proxyRequest(String request) { // todo annotations
            System.out.println(request);
            return request.length();
        }
    }

    public static class MyInterceptor {
        @RuntimeType
        public Object intercept(@This Object self,
                                @Origin Method method,
                                @AllArguments Object[] args,
                                @SuperMethod(nullIfImpossible = true) Method superMethod,
                                @Empty Object defaultValue) throws Throwable {
            try {
                if (superMethod==null) return defaultValue;
                return superMethod.invoke(self, args);
            } finally {
                // do your completion logic here
            }
        }
    }
    // @Pipe
}
