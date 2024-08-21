package RestServer.beans.statistics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/**
 * The {@code Statistics} class represents a collection of {@code StatisticBean} objects.
 * It implements the Singleton design pattern to ensure that only one instance
 * of this class is created. The class provides synchronized methods for managing
 * and retrieving statistics.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Statistics {

    @XmlElement(name="statistics")
    private final ArrayList<StatisticBean> statisticsList;
    private static Statistics instance;

    private Statistics() { statisticsList = new ArrayList<>(); }

    /**
     * Returns the singleton instance of the {@code Statistics} class.
     * The method is synchronized to ensure thread safety.
     */
    public synchronized static Statistics getInstance(){
        if(instance==null)
            instance = new Statistics();
        return instance;
    }

    /**
     * Returns a copy of the current list of statistics.
     * The method is synchronized to ensure thread safety.
     */
    public synchronized ArrayList<StatisticBean> getStatsList() {
        return new ArrayList<>(this.statisticsList);
    }

    public synchronized void addStatistic(StatisticBean s){
        statisticsList.add(s);
    }

    /**
     * Calculates the average pollution value from the most recent {@code n} statistics
     * for a specific robot identified by its ID.
     *
     * @param robotId the ID of the robot
     * @param n       the number of recent statistics to consider
     * @return the average pollution value, or 0.0 if no statistics are found
     */
    public double getRecentStatistics(int robotId, int n) {
        List<StatisticBean> allStats = getStatsList();
        List<StatisticBean> robotStats = new ArrayList<>();

        for (StatisticBean stat : allStats) {
            if (stat.getId() == robotId) {
                robotStats.add(stat);
            }
        }

        if (robotStats.isEmpty()) {
            return 0.0;
        }

        int start = Math.max(robotStats.size() - n, 0);
        double sum = 0.0;
        int count = 0;

        for (int i = start; i < robotStats.size(); i++) {
            List<Double> avgPollutionList = robotStats.get(i).getAvgPollution();
            count += avgPollutionList.size();
            for (Double avgPollution : avgPollutionList) {
                sum += avgPollution;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return sum / count;
    }

    /**
     * Calculates the average pollution value for all statistics recorded within a specified time range.
     *
     * @param t1 the start of the time range (inclusive), in milliseconds since epoch
     * @param t2 the end of the time range (inclusive), in milliseconds since epoch
     * @return the average pollution value, or -1 if no statistics are found in the specified interval
     */
    public double avgStatistics(long t1, long t2){
        List<StatisticBean> allStats = getStatsList();
        List<StatisticBean> timestampStats = new ArrayList<>();

        for (StatisticBean stat : allStats) {
            if (stat.getTimestamp() >= t1 && stat.getTimestamp() <= t2) {
                timestampStats.add(stat);
            }
        }
        if (timestampStats.isEmpty()) {
            return -1;
        }

        double sum = 0.0;
        int count = 0;

        for (StatisticBean timestampStat : timestampStats) {
            List<Double> avgPollutionList = timestampStat.getAvgPollution();
            count += avgPollutionList.size();
            for (Double avgPollution : avgPollutionList) {
                sum += avgPollution;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return sum / count;
    }

}
