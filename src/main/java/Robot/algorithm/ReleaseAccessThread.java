package Robot.algorithm;
import Robot.Robot;
import Robot.threads.QuitMessageThread;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.mechanic.MechanicGrpc;
import it.robot.mechanic.MechanicOuterClass.*;

import java.util.concurrent.TimeUnit;

import static Robot.utilities.RestMethods.removal;

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
            public void onNext(Empty value) {

            }

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

        try {
            channel.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Await termination error");
        }
    }

    private void sendQuitMessage(Robot sender, Robot receiver) {
        sender.getConnectedRobots().stream()
                .filter(r -> r.getId() != getId())
                .map(r -> new QuitMessageThread(receiver, r))
                .forEach(QuitMessageThread::start);
    }
}
