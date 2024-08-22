package RestServer;

import RestServer.configs.ServerConfig;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;


public class AdminServer {
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        startServer();
        stopServer();
    }

    private static void startServer() throws IOException {
        server = HttpServerFactory.create(ServerConfig.ADDRESS);
        server.start();
        System.out.printf("%s%s%n",
                ServerConfig.START_MESSAGE, ServerConfig.ADDRESS);
    }


    private static void stopServer() throws IOException {
        System.out.printf("%s%n",
                ServerConfig.RETURN_TO_STOP_MESSAGE);
        System.in.read();
        System.out.printf("%s%n",
                ServerConfig.STOP_START_MESSAGE);
        server.stop(0);
        System.out.printf("%s%n",
                ServerConfig.STOP_END_MESSAGE);
    }
}
