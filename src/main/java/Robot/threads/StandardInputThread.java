package Robot.threads;

import Grpc.configs.GrpcServerConfig;
import RestServer.configs.ServerConfig;
import Robot.Robot;
import Robot.algorithm.ReleaseAccessThread;
import Robot.algorithm.RequestMechanicThread;
import Robot.utilities.Colors;
import it.robot.mechanic.MechanicOuterClass.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * The {@code StandardInputThread} class is responsible for handling user input from the console
 * and managing various robot-related tasks based on the commands received. This class extends
 * {@link Thread} and overrides the {@code run()} method to listen for user input and initiate
 * specific robot operations, such as sending repair requests and handling quit requests.
 *
 * <p>The class also manages a timer thread that periodically generates a random number and,
 * based on certain conditions, triggers a gRPC call to handle robot repairs. The main thread
 * listens for user commands, such as "fix" or "quit", and processes them accordingly.</p>
 *
 * <p>This class is responsible for coordinating various robot states, including requesting
 * access for repairs, handling acknowledgments from other robots, and managing the robot's
 * availability status.</p>
 *
 * @see Robot
 * @see RequestMechanicThread
 * @see ReleaseAccessThread
 * @see Acknowledgement
 * @see RobotAccessRequest
 */
public class StandardInputThread extends Thread {
    private final Robot robot;
    private boolean isRunning = true;

    public StandardInputThread(Robot robot) {
        this.robot = robot;
    }

    public void run() {
        printStandardInputMessages();

        try (BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in))) {
            Thread timerThread = new Thread(() -> {
                try {
                    Thread.sleep(10000); //Wait for 10 seconds before the first execution
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                while (true) {
                    if (!robot.isRequestingAccess()) {
                        int randomNumber = new Random().nextInt(10)+1;
                        System.out.printf(Colors.ANSI_GRAY +
                                "\nRandom number generated from timer: %d\n"
                                + Colors.ANSI_RESET, randomNumber);

                        if (randomNumber == 7) {
                            makeGRPCCall();
                        }
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            timerThread.start();

            while (isRunning) {
                switch (inFromUser.readLine()) {
                    case "fix":
                        handleFixRequest();
                        break;
                    case "quit":
                        handleQuitRequest();
                        isRunning = false;
                        break;
                    default:
                        break;
                }
            }
        }   catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void printStandardInputMessages(){
        System.out.println(Colors.ANSI_GRAY + ServerConfig.STDIN_QUIT + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_GRAY + ServerConfig.STDIN_FIX + Colors.ANSI_RESET);
    }

    /**
     * Handles the "fix" command entered by the user. It initiates a gRPC call if the robot
     * is not currently requesting access.
     */
    private void handleFixRequest(){
        if (robot.isRequestingAccess()) {
            System.out.println(ServerConfig.FIX_REQUEST);
            return;
        }

        makeGRPCCall();
    }

    /**
     * Handles the "quit" command entered by the user. It gracefully shuts down the robot,
     * ensuring all operations are completed before stopping.
     */
    private void handleQuitRequest() throws InterruptedException {
        System.out.println(Colors.ANSI_GRAY + ServerConfig.QUIT_START + Colors.ANSI_RESET);
        robot.setQuitting(true);

        if(!robot.isAvailable())
            System.out.println(ServerConfig.QUIT_END);

        while(!robot.isAvailable()){
            synchronized (robot.getAvailableLock()) {
                robot.getAvailableLock().wait();
            }
        }

        robot.stop();
    }


     private void makeGRPCCall() {
            robot.setRequestingAccess(true);
            robot.setAvailable(false);

            long myTimestamp = robot.setMechanicTimestamp(System.currentTimeMillis());

            RobotAccessRequest request = RobotAccessRequest.newBuilder()
                    .setRobotId(robot.getId())
                    .setRobotPort(robot.getPort())
                    .setTimestamp(myTimestamp)
                    .build();

            try {
                sendMechanicRequest(request);
                awaitAllAcknowledgements();
                System.out.println(ServerConfig.REPAIR_START);

                robot.setRepairing(true);
                robot.setWaitingOK(false);

                System.out.println(ServerConfig.REPAIRING);

                try {
                    Thread.sleep(10000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(ServerConfig.REPAIR_END);

                robot.setRequestingAccess(false);
                robot.setRepairing(false);
                robot.setAvailable(true);

                sendAcknowledgementsToPendingRobots();

                //Notify availableLock
                synchronized (robot.getAvailableLock()) {
                    robot.getAvailableLock().notifyAll();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

    }



    private void sendMechanicRequest(RobotAccessRequest request) {
        List<Robot> connectedRobots = new ArrayList<>(robot.getConnectedRobots());
        robot.setMaxCount(connectedRobots.size());
        robot.setCurrentCount(0);

        for (Robot receiver : connectedRobots) {
            RequestMechanicThread requestAccess = new RequestMechanicThread(robot, receiver, request);
            requestAccess.start();
        }
    }

    private void awaitAllAcknowledgements() throws InterruptedException {
        robot.setWaitingOK(true);

        synchronized (robot.getCounterLock()){
            while(robot.getCurrentCount() < robot.getMaxCount()){
                robot.getCounterLock().wait();
            }
        }
    }

    /**
     * Sends acknowledgments to pending robots that have requested access during the repair process.
     * This method starts a {@link ReleaseAccessThread} for each pending robot.
     */
    private void sendAcknowledgementsToPendingRobots() {
        List<Robot> pendingRobots = new ArrayList<>(robot.takeMechanicRequests());
        Acknowledgement acknowledgement = Acknowledgement.newBuilder()
                .setRobotId(robot.getId())
                .build();

        if(!pendingRobots.isEmpty()){
            for (Robot robotReceiver : pendingRobots){
                ReleaseAccessThread releaseAccess = new ReleaseAccessThread(robot, robotReceiver, acknowledgement);
                releaseAccess.start();
            }
        }
    }
}
