package Robot.threads;

import Grpc.configs.GrpcServerConfig;
import Robot.Robot;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.heartbeat.HeartbeatGrpc;
import it.robot.heartbeat.HeartbeatGrpc.HeartbeatStub;
import com.google.protobuf.Empty;

import static Robot.utilities.RestMethods.removal;

/**
 * The {@code HeartBeatThread} class is responsible for sending a heartbeat signal from one robot
 * (the sender) to another robot (the receiver) over a gRPC connection.
 *
 * <p>This class utilizes a non-blocking gRPC stub to send the heartbeat and responds to
 * the result via a {@link StreamObserver}.
 *
 * @see ManagedChannel
 * @see StreamObserver
 */
public class HeartBeatThread extends Thread {
    private final Robot sender;
    private final Robot receiver;

    public HeartBeatThread(Robot sender, Robot receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Runs the thread, which establishes a gRPC connection to the receiver robot
     * and sends a heartbeat signal. The method handles the response via a
     * {@link StreamObserver}, dealing with both successful responses and errors.
     */
    public void run(){
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(String.format("%s:%d", receiver.getIp(), receiver.getPort()))
                        .usePlaintext().build();

        HeartbeatStub stub = HeartbeatGrpc.newStub(channel);
        Empty request = Empty.newBuilder().build();

        stub.heartbeat(request, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {}

            @Override
            public void onError(Throwable t) {
                handleHeartbeatError();
                channel.shutdown();
            }

            @Override
            public void onCompleted() {
                channel.shutdown();
            }
        });

        GrpcServerConfig.awaitChannelTermination(channel);
    }

    /**
     * Handles errors that occur during the heartbeat process. This method triggers the
     * sending of a quit message to all connected robots and removes the receiver robot
     * from the list of connected robots.
     */
    private void handleHeartbeatError(){
        sender.getConnectedRobots().forEach(r -> new QuitMessageThread(receiver, r).start());
        removal(receiver.getId());
    }
}
