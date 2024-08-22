package Simulators;

import Robot.Robot;
import Robot.threads.PublisherThread;

import java.util.ArrayList;
import java.util.List;

public class PollutionThread extends Thread {
    private final Robot robot;
    private final MeasurementsBuffer sensorBuffer;
    private final PM10Simulator pm10Simulator;
    private final List<Measurement> measurementBuffer;
    private final Object measurementBufferLock;
    private boolean isRunning;
    private final int WAIT_SECONDS = 15000;

    public PollutionThread(Robot robot) {
        this.robot = robot;
        sensorBuffer = new MeasurementsBuffer();
        pm10Simulator = new PM10Simulator(sensorBuffer);
        measurementBuffer = new ArrayList<>();
        measurementBufferLock = new Object();
        isRunning = true;
    }

    @Override
    public void run() {
        startPM10Simulator();
        Thread publisherThread = createPublisherThread();
        publisherThread.start();
        processMeasurements();
    }

    private void startPM10Simulator() {
        pm10Simulator.start();
    }

    private Thread createPublisherThread() {
        return new Thread(() -> {
            try {
                Thread.sleep(WAIT_SECONDS); //Wait for 15 seconds before the first publication
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                publishMeasurements();

                try {
                    Thread.sleep(WAIT_SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void publishMeasurements() {
        synchronized (measurementBufferLock) {
            if (!measurementBuffer.isEmpty()) {
                long currentTimestamp = System.currentTimeMillis();
                List<Measurement> measurementsToSend = new ArrayList<>(measurementBuffer);
                measurementBuffer.clear();
                new PublisherThread(robot, measurementsToSend, currentTimestamp).start();
            }
        }
    }

    private void processMeasurements() {
        while (isRunning) {
            try {
                List<Measurement> measurements = sensorBuffer.readAllAndClean();
                Measurement averageMeasurement = computeAverageMeasurement(measurements);
                addMeasurement(averageMeasurement);
            } catch (InterruptedException e) {
                isRunning = false;
                System.err.println("Measurement processing interrupted");
            }
        }
    }

    private Measurement computeAverageMeasurement(List<Measurement> measurements) {
        double sum = 0;
        String type = "";
        long timestamp = 0;
        int size = measurements.size();

        for (Measurement measurement : measurements) {
            sum += measurement.getValue();
            type = measurement.getType();
            timestamp += measurement.getTimestamp();
        }

        double average = sum / size;
        Measurement avgMeasurement = new Measurement(String.valueOf(robot.getId()), type, average, timestamp);
        measurements.add(avgMeasurement);

        return avgMeasurement;
    }

    private void addMeasurement(Measurement measurement) {
        synchronized (measurementBufferLock) {
            measurementBuffer.add(measurement);
        }
    }
}
