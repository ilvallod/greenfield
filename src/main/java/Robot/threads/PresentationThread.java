package Robot.threads;

import Grpc.configs.GrpcServerConfig;
import Robot.Robot;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.presentation.PresentationGrpc;
import it.robot.presentation.PresentationGrpc.*;
import it.robot.presentation.PresentationOuterClass.*;

/**
 * The {@code PresentationThread} class is responsible for sending presentation data from one robot
 * (the sender) to another robot (the receiver) over a gRPC connection.
 *
 * <p>The presentation data includes the sender's ID, port, position (X and Y coordinates),
 * and district information. This data is sent using a non-blocking gRPC stub, and the response
 * is managed using a {@link StreamObserver}.</p>
 *
 * @see ManagedChannel
 * @see StreamObserver
 * @see PresentationGrpc
 * @see PresentationData
 */
public class PresentationThread extends Thread {
    private final Robot sender;
    private final Robot receiver;

    public PresentationThread(Robot sender, Robot receiver){
        this.sender = sender;
        this.receiver = receiver;
    }

    public void run(){
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(String.format("%s:%d", receiver.getIp(), receiver.getPort()))
                .usePlaintext().build();

        PresentationStub stub = PresentationGrpc.newStub(channel);

        PresentationData request = PresentationData.newBuilder()
                .setId(sender.getId())
                .setPort(sender.getPort())
                .setPosition(
                        Position.newBuilder()
                                .setX(sender.getX())
                                .setY(sender.getY())
                                .build())
                .setDistrict(sender.getDistrict())
                .build();

        stub.presentation(request, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {}

            @Override
            public void onError(Throwable t) {
                System.out.println("Presentation error");
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
