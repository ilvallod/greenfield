package Robot;

import Grpc.GrpcServer;
import RestServer.configs.ServerConfig;
import Robot.threads.HeartBeatMonitorThread;
import Robot.threads.StandardInputThread;
import Robot.threads.PresentationThread;
import Robot.utilities.Colors;
import Simulators.PollutionThread;

import java.util.InputMismatchException;
import java.util.Scanner;

import static Robot.utilities.RestMethods.insertion;

/**
 * The StartRobot class is the entry point for starting a robot.
 * It initializes a robot with an ID and port, then starts various threads
 * necessary for the robot's operation, including gRPC server, standard input handling,
 * heartbeat monitoring, and pollution monitoring.
 */
public class StartRobot {
    public static void main(String[] args) {
        int id = getValidIntInput(ServerConfig.ROBOT_INPUT_ID);
        int port = getValidIntInput(ServerConfig.ROBOT_INPUT_PORT);

        Robot robot = new Robot(id, "localhost", port);

        if(insertion(robot)) {
            startPresentationThread(robot);
            startServerThreads(robot);
        }
    }

    private static int getValidIntInput (String prompt){
        Scanner sc = new Scanner(System.in);
        int input;
        while (true) {
            System.out.print(prompt);
            try {
                input = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println(Colors.ANSI_GRAY + ServerConfig.ROBOT_INPUT_ERROR + Colors.ANSI_RESET);
                sc.nextLine(); // clear scanner buffer
            }
        }
        return input;
    }

    /**
     * Starts the presentation threads for each connected robot, excluding the robot itself.
     * Each thread handles the presentation tasks between the current robot and another connected robot.
     *
     * @param robot The robot for which presentation threads are to be started.
     */
    private static void startPresentationThread(Robot robot){
        robot.getConnectedRobots().stream()
                .filter(receiver -> receiver.getId() != robot.getId())
                .map(receiver -> new PresentationThread(robot, receiver))
                .forEach(PresentationThread::start);
    }

    private static void startServerThreads(Robot robot){
        new GrpcServer(robot).start();
        new StandardInputThread(robot).start();
        new HeartBeatMonitorThread(robot, 2000).start();
        new PollutionThread(robot).start();
    }
}
