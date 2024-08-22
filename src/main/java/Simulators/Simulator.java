package Simulators;

import java.util.Random;

/**
 * An abstract class representing a generic simulator that generates measurements.
 * This class extends {@link Thread}, allowing each simulator to run its own thread.
 * Subclasses must implement the {@link #run()} method to define the specific behavior of the simulator.
 */
public abstract class Simulator extends Thread {

    protected volatile boolean stopCondition = false;
    protected Random rnd = new Random();
    private final Buffer buffer; // The buffer to which measurements will be added.
    private final String id;
    private final String type;

    public Simulator(String id, String type, Buffer buffer){
        this.id = id;
        this.type = type;
        this.buffer = buffer;
    }

    public void stopMeGently() {
        stopCondition = true;
    }

    protected void addMeasurement(double measurement){
        buffer.addMeasurement(new Measurement(id, type, measurement, currentTime()));
    }

    public Buffer getBuffer(){
        return buffer;
    }

    protected void sensorSleep(long milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void run();

    private long currentTime(){
        return System.currentTimeMillis();
    }

    public String getIdentifier(){
        return id;
    }

}

