package Robot.algorithm;

import Grpc.configs.GrpcServerConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.mechanic.MechanicGrpc;
import it.robot.mechanic.MechanicOuterClass;
import it.robot.mechanic.MechanicOuterClass.RobotAccessRequest;
import Robot.Robot;

/**
 * The {@code RequestMechanicThread} class is responsible for sending a request for mechanic access
 * from one robot (the sender) to another robot (the receiver) over a gRPC connection.
 *
 * <p>The response from the receiver robot is handled asynchronously. If the receiver grants
 * access, the sender's acknowledgment counter is incremented. Once all acknowledgments are received,
 * the sender is notified to proceed with the next steps.</p>
 *
 * @see ManagedChannel
 * @see StreamObserver
 * @see MechanicGrpc
 * @see RobotAccessRequest
 * @see MechanicOuterClass.Acknowledgement
 */
public class RequestMechanicThread extends Thread {
    private final Robot sender;
    private final Robot receiver;
    private final RobotAccessRequest request;

    public RequestMechanicThread(Robot sender, Robot receiver, RobotAccessRequest request) {
        this.sender = sender;
        this.receiver = receiver;
        this.request = request;
    }

    @Override
    public void run() {
        final ManagedChannel channel =
                ManagedChannelBuilder.forTarget(String.format("%s:%d", receiver.getIp(), receiver.getPort()))
                        .usePlaintext().build();

        MechanicGrpc.MechanicStub stub = MechanicGrpc.newStub(channel);
        stub.requestAccess(request, new StreamObserver<MechanicOuterClass.Acknowledgement>() {
            @Override
            public void onNext(MechanicOuterClass.Acknowledgement value) {
                if (value.getAck().equals("Grant")) {
                    synchronized (sender.getCounterLock()) {
                        sender.incrementCounter();
                        if (sender.getCurrentCount() == sender.getMaxCount()) {
                            sender.getCounterLock().notifyAll();
                        }
                    }
                }
            }

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
