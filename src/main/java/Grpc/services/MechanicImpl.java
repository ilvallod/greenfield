package Grpc.services;

import Robot.Robot;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import it.robot.mechanic.MechanicGrpc.*;
import it.robot.mechanic.MechanicOuterClass.*;

/**
 * The {@code MechanicImpl} class is a gRPC service implementation that handles requests for access
 * to a shared resource in a distributed system. The service manages mutual exclusion using
 * Ricart and Agrawala algorithm implementation.
 */
public class MechanicImpl extends MechanicImplBase {
    private final Robot robot;

    public MechanicImpl(Robot robot) {
        this.robot = robot;
    }

    /**
     * Handles incoming requests for access to the shared resource.
     * The method determines whether to grant or queue the request based on the robot's current state
     * and the timestamp of the request.
     *
     * @param request           the {@link RobotAccessRequest} containing the request details, including the requester's ID and timestamp.
     * @param responseObserver  the {@link StreamObserver} used to send the response back to the requester.
     */
    @Override
    public void requestAccess(RobotAccessRequest request, StreamObserver<Acknowledgement> responseObserver) {
        System.out.printf("\n[MUTEX] Request received from %d with timestamp %d\n",
                request.getRobotId(), request.getTimestamp());

        if (request.getRobotId() == robot.getId()) {
            sendOkResponse(responseObserver);
        } else {
            // FIRST CASE: If Q neither uses nor requires R, it answers OK
            if (!robot.isRepairing() && !robot.isRequestingAccess()) {
                sendOkResponse(responseObserver);

            // SECOND CASE: If Q is using R, it does not answer and queues the request
            } else if (robot.isRepairing()) {
                System.out.printf("\n[MUTEX] Received request from %d, adding to queue\n", request.getRobotId());
                queueRequest(request);

            // THIRD CASE: If Q wants to use R but has not yet, it compares the timestamps, and the earliest wins
            } else if (robot.isRequestingAccess() && !robot.isRepairing()) {
                if (request.getTimestamp() <= robot.getMechanicTimestamp()) {
                    sendOkResponse(responseObserver);

                } else {
                    System.out.printf("\n[MUTEX] Received request from %d , adding to queue\n", request.getRobotId());
                    queueRequest(request);
                }
            }
        }
    }

    private void sendOkResponse(StreamObserver<Acknowledgement> responseObserver) {
        responseObserver.onNext(Acknowledgement.newBuilder().setAck("Grant").build());
        responseObserver.onCompleted();
    }

    private void queueRequest(RobotAccessRequest request) {
        robot.putInMechanicRequests(new Robot(request.getRobotId(), "localhost", request.getRobotPort()));
    }


    /**
     * Handles the release of access to the shared resource.
     * The method increments the robot's counter when a release acknowledgment is received.
     *
     * @param request           the {@link Acknowledgement} received from the requester.
     * @param responseObserver  the {@link StreamObserver} used to send the empty response back to the requester.
     */
    @Override
    public void releaseAccess(Acknowledgement request, StreamObserver<Empty> responseObserver) {
        synchronized (robot.getCounterLock()) {
            robot.incrementCounter();
        }
        System.out.printf("\n[MUTEX] Received ok from %d\n", request.getRobotId());

        Empty empty = Empty.newBuilder().build();
        responseObserver.onNext(empty);
        responseObserver.onCompleted();
    }
}

