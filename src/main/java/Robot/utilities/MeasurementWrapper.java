package Robot.utilities;

import Simulators.Measurement;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class that encapsulates a set of measurements associated with a specific robot.
 * It includes the robot's ID, the list of measurements, and the current timestamp.
 */
public class MeasurementWrapper {
    private final int robotId;
    private final List<Measurement> measurements;
    private final long currentTimestamp;

    public MeasurementWrapper(int robotId, List<Measurement> measurements, long currentTimestamp) {
        this.robotId = robotId;
        this.measurements = measurements;
        this.currentTimestamp = currentTimestamp;
    }

    public int getRobotId() { return robotId; }
    public List<Measurement> getMeasurements() { return measurements; }
    public long getCurrentTimestamp() { return currentTimestamp; }

    public List<Double> getValues() {
        List<Double> values = new ArrayList<>();
        for (Measurement m : measurements) {
            values.add(m.getValue());
        }
        return values;
    }

    @Override
    public String toString() {
        //TODO: remove hardcoded text
        return String.format(
                Colors.ANSI_GRAY +"\nAverages from: %d\n%s\nTimestamp: %d\n\n" +Colors.ANSI_RESET,
                getRobotId(), getValues().toString(), getCurrentTimestamp());
    }
}
