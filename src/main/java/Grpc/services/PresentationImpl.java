package Grpc.services;

import Robot.Robot;
import Robot.utilities.Colors;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import it.robot.presentation.PresentationGrpc.*;
import it.robot.presentation.PresentationOuterClass.*;

/**
 * The {@code PresentationImpl} class is a gRPC service implementation that handles presentation
 * requests from robots. This service processes incoming presentation data, logs the information,
 * and adds the presenting robot to the list of connected robots.
 */
public class PresentationImpl extends PresentationImplBase {
    private final Robot robot;

    public PresentationImpl(Robot robot) {
        this.robot = robot;
    }

    /**
     * The method logs the robot's information, including its ID, port,
     * position, and district, and then adds the robot to the list of connected robots.
     *
     * @param request           the {@link PresentationData} containing the presenting robot's details.
     * @param responseObserver  the {@link StreamObserver} used to send the response back to the presenting robot.
     */
    @Override
    public void presentation(PresentationData request, StreamObserver<Empty> responseObserver) {
        System.out.printf(Colors.ANSI_GREEN
                        + "\nPresentation received from Robot %d - port: %d - Position [%d,%d] in %s\n"
                        + Colors.ANSI_RESET,
                request.getId(), request.getPort(), request.getPosition().getX(),
                request.getPosition().getY(), request.getDistrict());

        robot.addToConnectedRobots(request.getId(), "localhost", request.getPort());

        Empty response = Empty.getDefaultInstance();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
