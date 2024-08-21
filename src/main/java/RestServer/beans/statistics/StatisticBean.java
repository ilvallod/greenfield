package RestServer.beans.statistics;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * The {@code StatisticBean} class represents statistical data including an ID,
 * average pollution values, and a timestamp.
 * This class is used for storing and manipulating statistics related to pollution.
 */
@XmlRootElement
public class StatisticBean {
    private int id;
    private List<Double> avgPollution;
    private long timestamp;

    /** Default constructor. */
    public StatisticBean(){}

    public StatisticBean(int id, List<Double> avgPollution, long timestamp) {
        this.id = id;
        this.avgPollution = avgPollution;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<Double> getAvgPollution() { return avgPollution; }
    public void setAvgPollution(List<Double> avgPollution) { this.avgPollution = avgPollution; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
