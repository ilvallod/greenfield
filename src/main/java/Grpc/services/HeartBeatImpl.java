package Grpc.services;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import it.robot.heartbeat.HeartbeatGrpc.HeartbeatImplBase;
import Robot.Robot;

/**
 * The {@code HeartBeatImpl} class is a gRPC service implementation that handles heartbeat requests.
 * This class extends {@link HeartbeatImplBase}, which is generated from the gRPC service definition.
 * It provides a simple implementation that responds to heartbeat requests with an empty response.
 */
public class HeartBeatImpl extends HeartbeatImplBase {

    public HeartBeatImpl(Robot robot) {}

    @Override
    public void heartbeat(Empty request, StreamObserver<Empty> responseObserver) {
        Empty response = Empty.getDefaultInstance();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
