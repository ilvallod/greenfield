package Grpc.services;

import Robot.Robot;
import Robot.utilities.Colors;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import it.robot.quit.QuitGrpc.QuitImplBase;
import it.robot.quit.QuitOuterClass.QuitRequest;

/**
 * The {@code QuitImpl} class is a gRPC service implementation that handles quit requests from robots.
 * This service processes incoming quit messages, updates the robot's state, and removes the quitting robot
 * from the list of connected robots.
 */
public class QuitImpl extends QuitImplBase {
    private final Robot robot;

    public QuitImpl(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void quit(QuitRequest request, StreamObserver<Empty> responseObserver){
        System.out.printf(
                Colors.ANSI_GRAY + "\n[QUIT] Quit message received from robot %d\n" + Colors.ANSI_RESET, request.getRobotId());

        if(robot.isWaitingOK())
            robot.incrementCounter(); //Add ok
        robot.removeFromConnectedRobots(request.getRobotId());

        Empty empty = Empty.newBuilder().build();
        responseObserver.onNext(empty);
        responseObserver.onCompleted();
    }
}
