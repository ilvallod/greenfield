package RestServer;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class AdminServer {
    private static final int SERVER_PORT = 1337;
    private static final String SERVER_ADDRESS = String.format("http://localhost:%d/", SERVER_PORT);
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        startServer();
        stopServer();
    }

    private static void startServer() throws IOException {
        server = HttpServerFactory.create(SERVER_ADDRESS);
        server.start();
        System.out.printf("Server started on: %s%n", SERVER_ADDRESS);
    }


    private static void stopServer() throws IOException {
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}
