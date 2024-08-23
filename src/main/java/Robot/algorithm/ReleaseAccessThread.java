package Robot.algorithm;
import Grpc.configs.GrpcServerConfig;
import Robot.Robot;
import Robot.threads.QuitMessageThread;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.mechanic.MechanicGrpc;
import it.robot.mechanic.MechanicOuterClass.*;

import static Robot.utilities.RestMethods.removal;

/**
 * The {@code ReleaseAccessThread} class is responsible for releasing access to a robot after
 * it has completed a mechanic operation. This thread sends an acknowledgment message from the
 * sender robot to the receiver robot over a gRPC connection, indicating that the sender has
 * released access and the receiver can proceed with its operations.
 *
 * <p>If the receiver robot is unresponsive or crashes, this thread also
 * handles removing the receiver from the queue and sending a quit message to other connected robots.</p>
 *
 * @see ManagedChannel
 * @see StreamObserver
 * @see MechanicGrpc
 * @see Acknowledgement
 */
public class ReleaseAccessThread extends Thread {
    private final Robot sender;
    private final Robot receiver;
    private final Acknowledgement acknowledgement;

    public ReleaseAccessThread(Robot sender, Robot receiver, Acknowledgement acknowledgement) {
        this.sender = sender;
        this.receiver = receiver;
        this.acknowledgement = acknowledgement;
    }

    @Override
    public void run() {
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(String.format("%s:%d", receiver.getIp(), receiver.getPort()))
                        .usePlaintext().build();

        MechanicGrpc.MechanicStub stub = MechanicGrpc.newStub(channel);
        stub.releaseAccess(acknowledgement, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {}

            //If a robot in queue crashes
            @Override
            public void onError(Throwable t) {
                sender.removeFromMechanicRequests(receiver); //Remove from queue
                sendQuitMessage(sender, receiver);
                removal(receiver.getId());
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
     * Sends a quit message to all connected robots, excluding the sender, to notify them
     * that the receiver robot is no longer available.
     *
     * @param sender the robot that is sending the quit messages
     * @param receiver the robot that is being removed from the network
     */
    private void sendQuitMessage(Robot sender, Robot receiver) {
        sender.getConnectedRobots().stream()
                .filter(r -> r.getId() != getId())
                .map(r -> new QuitMessageThread(receiver, r))
                .forEach(QuitMessageThread::start);
    }
}
