package Robot.threads;

import Grpc.configs.GrpcServerConfig;
import Robot.Robot;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.quit.QuitGrpc;
import it.robot.quit.QuitGrpc.QuitStub;
import it.robot.quit.QuitOuterClass.QuitRequest;

/**
 * The {@code QuitMessageThread} class is responsible for sending a quit message from one robot
 * (the sender) to another robot (the receiver) over a gRPC connection.
 *
 * <p>The quit message indicates that the sender robot is disconnecting or shutting down.
 * The message is sent using a non-blocking gRPC stub, and the response is managed using
 * a {@link StreamObserver}.</p>
 *
 * @see ManagedChannel
 * @see StreamObserver
 * @see QuitGrpc
 * @see QuitRequest
 */
public class QuitMessageThread extends Thread {
    private final Robot sender;
    private final Robot receiver;

    public QuitMessageThread(Robot sender, Robot receiver){
        this.sender = sender;
        this.receiver = receiver;
    }

    public void run() {
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(String.format("%s:%d", receiver.getIp(), receiver.getPort()))
                        .usePlaintext().build();

        QuitStub stub = QuitGrpc.newStub(channel);

        QuitRequest request = QuitRequest.newBuilder()
                .setRobotId(sender.getId())
                .build();

        stub.quit(request, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {}

            @Override
            public void onError(Throwable t) {
                channel.shutdown();
            }

            @Override
            public void onCompleted() {
                channel.shutdown();
            }
        });

        GrpcServerConfig.awaitChannelTermination(channel);
    }
}
