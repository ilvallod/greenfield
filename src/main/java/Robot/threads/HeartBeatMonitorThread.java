package Robot.threads;
import Robot.Robot;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code HeartBeatMonitorThread} class is responsible for periodically sending heartbeat signals
 * from one robot to all other connected robots.
 *
 * <p>The heartbeats are sent by creating and starting a new {@link HeartBeatThread}
 * for each connected robot.</p>
 *
 * <p>The interval between heartbeats is specified in milliseconds and passed to this
 * class through the constructor.</p>
 *
 * @see HeartBeatThread
 */
public class HeartBeatMonitorThread extends Thread {
    private final Robot robot;
    private final long interval;

    public HeartBeatMonitorThread(Robot robot, long interval) {
        this.robot = robot;
        this.interval = interval;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
                List<Robot> connectedRobots = new ArrayList<>(robot.getConnectedRobots());
                for (Robot receiver : connectedRobots) {
                    HeartBeatThread heartbeat = new HeartBeatThread(robot, receiver);
                    heartbeat.start();
                }

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
