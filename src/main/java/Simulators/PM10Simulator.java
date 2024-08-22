package Simulators;

/**
 * A simulator for generating PM10 air quality measurements.
 * The simulator produces values based on a sinusoidal function
 * with added noise to simulate real-world variability.
 * <p>
 * The simulator runs in its own thread, periodically generating
 * new PM10 measurements and adding them to the provided buffer.
 */
public class PM10Simulator extends Simulator {

    private static final double A = 15; // Amplitude of the sine wave
    private static final double W = 0.05; // Angular frequency of the sine wave
    private static int ID = 1;

    private static final int BASE_WAIT_TIME = 200;
    private static final int RANDOM_WAIT_RANGE = 200;
    private static final double STEP_INCREMENT = 0.2;
    private static final double BASE_PM10_VALUE = 15;

    public PM10Simulator(String id, Buffer buffer){
        super(id, "PM10", buffer);
    }

    public PM10Simulator(Buffer buffer){
        this("pm10-"+(ID++), buffer);
    }

    /**
     * The main logic of the simulator. Continuously generates PM10 measurements
     * based on a sinusoidal function with added Gaussian noise and sleeps for
     * a random amount of time between measurements until the stop condition is met.
     */
    @Override
    public void run() {
        double i = rnd.nextInt();
        long waitingTime;

        while(!stopCondition){
            double pm10 = calculatePM10Value(i);
            addMeasurement(pm10);
            waitingTime = BASE_WAIT_TIME + (int)(Math.random()*RANDOM_WAIT_RANGE);
            sensorSleep(waitingTime);
            i+=STEP_INCREMENT;
        }
    }

    /**
     * Calculates a simulated PM10 value based on the given time parameter.
     * The value is generated using a sine function with amplitude A and angular frequency W,
     * plus Gaussian noise, and a base PM10 value.
     *
     * @param t the time parameter used in the sine function
     * @return the calculated PM10 value
     */
    private double calculatePM10Value(double t){
        return Math.abs(A * Math.sin(W*t) + rnd.nextGaussian()*0.1)+BASE_PM10_VALUE;
    }
}
