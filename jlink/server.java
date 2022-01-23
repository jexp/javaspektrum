import com.sun.net.httpserver.*;

public class server {

public static void main(String...args) {
  var server = SimpleFileServer.createFileServer(new InetSocketAddress(8080), Path.of("."), OutputLevel.VERBOSE);
  server.start();
}
}
