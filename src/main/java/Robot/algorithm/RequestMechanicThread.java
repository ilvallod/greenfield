package Robot.algorithm;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.robot.mechanic.MechanicGrpc;
import it.robot.mechanic.MechanicOuterClass;
import it.robot.mechanic.MechanicOuterClass.RobotAccessRequest;
import Robot.Robot;

import java.util.concurrent.TimeUnit;

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

        try {
            channel.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Await termination error");
        }
    }
}
