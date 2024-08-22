package Simulators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A buffer that stores {@link Measurement} objects and allows for adding measurements
 * and reading all measurements once a specified window size is reached.
 * This class is thread-safe and uses a synchronized block to handle concurrent access.
 */
public class MeasurementsBuffer implements Buffer {
    private final static int WINDOW_SIZE = 8;
    private final static double OVERLAP_RATIO = 0.5;

    private final List<Measurement> buffer;
    private final Object bufferLock;

    /**
     * Constructs a new MeasurementsBuffer with a fixed window size and overlap ratio.
     * The buffer is initialized as a {@link LinkedList} to efficiently handle addition
     * and removal of measurements.
     */
    public MeasurementsBuffer() {
        buffer = new LinkedList<>();
        bufferLock = new Object();
    }

    /**
     * Adds a measurement to the buffer. If the buffer reaches the window size,
     * any waiting threads are notified.
     *
     * @param measurement the {@link Measurement} to be added to the buffer
     */
    @Override
    public void addMeasurement(Measurement measurement) {
        synchronized (bufferLock) {
            buffer.add(measurement);
            bufferLock.notify();
        }
    }

    /**
     * Reads all measurements from the buffer once the buffer reaches the defined window size.
     * The method blocks until the buffer contains the required number of measurements.
     * After reading, it clears a portion of the buffer based on the overlap ratio.
     *
     * @return a list of {@link Measurement} objects containing all measurements in the buffer
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Override
    public List<Measurement> readAllAndClean() throws InterruptedException {
        ArrayList<Measurement> measurements;
        synchronized (bufferLock) {
            while (buffer.size() != WINDOW_SIZE) {
                bufferLock.wait();
            }

            measurements = new ArrayList<>(buffer);
            buffer.subList(0, (int) (WINDOW_SIZE * OVERLAP_RATIO)).clear();
        }
        return measurements;
    }
}
