// java --enable-preview --source 17 LoomServer.java
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class LoomServer {
    static byte[] RESPONSE = """
    HTTP/1.1 200 OK
    Content-Length: 12

    Hello Loom\r
    """.getBytes();

    public static void main(String...args) throws IOException {
        System.out.println(new String(RESPONSE));
        try (var ss = new ServerSocket(2000);
             var pool = Executors.newVirtualThreadExecutor()) {
             while (true) {
                var socket = ss.accept();
                pool.execute(() -> {
                    try (var s = socket; 
                        var in = s.getInputStream(); 
                        var out = s.getOutputStream()) {
                            byte b=-1,b1=-1,b2=-1;
                            while ((b = (byte)in.read()) != -1) {
                                if (b==10 && b1 == 13 && b2 == 10) break;
                                b2=b1; b1=b;
                                // out.write(b+1);
                            }
                            out.write(RESPONSE);
                        } catch(IOException ioe) {}
                });
             }
        }
    }
}