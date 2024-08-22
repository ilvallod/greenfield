package Robot;

import RestServer.beans.robots.RobotBean;
import java.util.ArrayList;

/**
 * The Robot class represents a robot entity with attributes like id, ip, port, position,
 * and a list of connected robots. It also handles mutual exclusion mechanisms and
 * manages requests for mechanics.
 */
public class Robot {

    //INITIALIZATION
    private int id;
    private String ip;
    private int port;
    private int[] position;
    private ArrayList<RobotBean> robotsBeanList;
    private ArrayList<Robot> connectedRobots;
    private String district;
    private boolean isAvailable;
    private Object availableLock;

    //MUTUAL EXCLUSION
    private boolean requestingAccess;
    private boolean repairing;
    private int maxCount;
    private int currentCount;
    private final Object counterLock;
    private ArrayList<Robot> mechanicRequests;
    private long mechanicTimestamp;
    private boolean waitingOK;

    //QUIT
    private boolean isQuitting;

    public Robot(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        robotsBeanList = new ArrayList<>();
        connectedRobots = new ArrayList<>();
        requestingAccess = false;
        isQuitting = false;
        mechanicRequests = new ArrayList<>();
        isAvailable = true;
        availableLock = new Object();
        waitingOK = false;
        counterLock = new Object();
        maxCount = 0;
        currentCount = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public int[] getPosition() { return position; }
    public void setPosition(int[] position) { this.position = position; }

    /** Gets the x-coordinate of the robot's position. **/
    public int getX() { return position[0]; }
    public int getY() { return position[1]; }

    public ArrayList<RobotBean> getRobotsBeanList() { return robotsBeanList; }
    public void setRobotsBeanList(ArrayList<RobotBean> robotsList) { this.robotsBeanList = robotsList; }

    public ArrayList<Robot> getConnectedRobots() { return connectedRobots; }
    public void setConnectedRobots(ArrayList<Robot> connectedRobots) { this.connectedRobots = connectedRobots; }

    /** Checks if the robot is currently requesting access. **/
    public boolean isRequestingAccess() { return requestingAccess; }
    public void setRequestingAccess(boolean requestingAccess) { this.requestingAccess = requestingAccess; }

    /** Checks if the robot is currently under repair. **/
    public boolean isRepairing() { return repairing; }
    public void setRepairing(boolean repairing) { this.repairing = repairing; }

    public boolean isQuitting() { return isQuitting; }
    public void setQuitting(boolean quitting) { isQuitting = quitting; }

    /** Gets the lock object used for mutual exclusion on the counter. **/
    public Object getCounterLock() { return counterLock; }
    public void setCurrentCount(int currentCount) { this.currentCount = currentCount; }

    public ArrayList<Robot> getMechanicRequests(){ return mechanicRequests; }
    public void setMechanicRequests(ArrayList<Robot> mechanicRequests){ this.mechanicRequests = mechanicRequests; }

    public int getMaxCount() {
        synchronized (counterLock) {
            return maxCount;
        }
    }

    public void setMaxCount(int maxCount) {
        synchronized (counterLock) {
            this.maxCount = maxCount;
        }
    }

    public int getCurrentCount() {
        synchronized (counterLock) {
            return currentCount;
        }
    }

    public long getMechanicTimestamp() { return mechanicTimestamp; }
    public long setMechanicTimestamp(long mechanicTimestamp) {
        this.mechanicTimestamp = mechanicTimestamp;
        return mechanicTimestamp;
    }

    /** Gets the district the robot is located in. **/
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    /** Gets the lock object used for mutual exclusion on the availability status. **/
    public Object getAvailableLock() { return availableLock; }
    public void setAvailableLock(Object availableLock) { this.availableLock = availableLock; }

    public boolean isWaitingOK() { return waitingOK; }
    public void setWaitingOK(boolean waitingOK) { this.waitingOK = waitingOK; }

    public void addToConnectedRobots(int id, String ip, int port){
        Robot robotToAdd = new Robot(id, ip, port);
        connectedRobots.add(robotToAdd);
    }

    public void removeFromConnectedRobots(int id){
        connectedRobots.removeIf(robot -> robot.getId() == id);
    }

    public void stop(){
        System.exit(0);
    }

    public synchronized ArrayList<Robot> takeMechanicRequests(){
        ArrayList<Robot> queueCopy = new ArrayList<>(getMechanicRequests());
        mechanicRequests.clear();
        return queueCopy;
    }

    public synchronized void putInMechanicRequests(Robot robot) {
        mechanicRequests.add(robot);
    }

    public synchronized void removeFromMechanicRequests(Robot robot) {
        mechanicRequests.remove(robot);
    }

    /**
     * Increments the counter value. This method is synchronized to ensure thread safety.
     * It also notifies all waiting threads that the counter has been modified.
     */
    public void incrementCounter() {
        synchronized (counterLock) {
            currentCount++;
            counterLock.notifyAll();
        }
    }
}
