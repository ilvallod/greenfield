package Grpc.configs;

import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public class GrpcServerConfig {
    public static final String ERROR_STARTING = "Error while starting GRPC server";
    public static final String SERVER_STOPPED = "GRPC server stopped";

    public static final String AWAIT_TERMINATION_ERROR = "Await termination error";

    public static void awaitChannelTermination(ManagedChannel channel) {
        try {
            channel.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println(GrpcServerConfig.AWAIT_TERMINATION_ERROR);
        }
    }
}