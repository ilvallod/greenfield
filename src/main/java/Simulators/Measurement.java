package Simulators;

/**
 * Represents a measurement with an associated ID, type, value, and timestamp.
 * This class implements {@link Comparable} to allow measurements to be compared based on their timestamp.
 */
public class Measurement implements Comparable<Measurement> {

    private String id;
    private String type;
    private double value;
    private long timestamp;

    public Measurement(String id, String type, double value, long timestamp) {
        this.id=id;
        this.type=type;
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public int compareTo(Measurement m) {
        return Long.compare(this.timestamp, m.timestamp);
    }

    @Override
    public String toString() {
        return value + " " + timestamp;
    }
}
