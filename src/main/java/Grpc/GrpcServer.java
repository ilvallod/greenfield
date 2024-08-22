package Grpc;

import Grpc.configs.GrpcServerConfig;
import Grpc.services.HeartBeatImpl;
import Grpc.services.MechanicImpl;
import Grpc.services.PresentationImpl;
import Grpc.services.QuitImpl;
import Robot.Robot;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * The {@code GrpcServer} class represents a gRPC server that manages various services.
 * This class extends {@link Thread} to run the server in a separate thread.
 */
public class GrpcServer extends Thread {
    private final Robot robot;
    private Server server;

    public GrpcServer(Robot robot) {
        this.robot = robot;
    }

    public void run(){
        try {
            startServer();
            waitForTermination();
        } catch (IOException e) {
            System.out.println(GrpcServerConfig.ERROR_STARTING);
        } catch (InterruptedException e) {
            handleInterruption();
        }
    }

    /**
     * Initializes and starts the gRPC server on the port provided by the {@link Robot}.
     * The server is configured with various services such as {@link PresentationImpl}, {@link MechanicImpl},
     * {@link QuitImpl}, and {@link HeartBeatImpl}.
     */
    private void startServer() throws IOException {
        server = ServerBuilder.forPort(robot.getPort())
                .addService(new PresentationImpl(robot))
                .addService(new MechanicImpl(robot))
                .addService(new QuitImpl(robot))
                .addService(new HeartBeatImpl(robot))
                .build();
        server.start();
    }

    private void waitForTermination() throws InterruptedException {
        server.awaitTermination();
    }

    private void handleInterruption() {
        server.shutdown();
        System.out.println(GrpcServerConfig.SERVER_STOPPED);
    }
}