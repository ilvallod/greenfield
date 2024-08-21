package RestServer.beans.robots;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 * The {@code Robots} class represents a collection of {@code RobotBean} objects.
 * It implements the Singleton design pattern to ensure that only one instance
 * of this class is created. The class also provides synchronized methods for
 * managing the list of robots.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Robots {

    @XmlElement(name="robots")
    private final ArrayList<RobotBean> robotsList;
    private static Robots instance;

    private Robots() { robotsList = new ArrayList<>(); }

    /**
     * Returns the singleton instance of the {@code Robots} class.
     * The method is synchronized to ensure thread safety.
     */
    public synchronized static Robots getInstance(){
        if(instance==null)
            instance = new Robots();
        return instance;
    }

    /**
     * Returns a copy of the current list of robots.
     * The method is synchronized to ensure thread safety.
     */
    public synchronized ArrayList<RobotBean> getRobotsList() {
        return new ArrayList<>(this.robotsList);
    }

    public synchronized MyResponse add(RobotBean robotBean){
        int idToAdd = robotBean.getId();
        int portToAdd = robotBean.getListeningPort();

        String district = selectDistrict(getRobotsList()); //Select a district with the fewest robots
        int[] position = getPositionInDistrict(district); //Generate position in that district

        for (RobotBean existingRobot : robotsList) {
            if (existingRobot.getId() == idToAdd || existingRobot.getListeningPort() == portToAdd) { //Duplicate robot
                return null;
            }
        }

        robotBean.setPosition(position);
        robotBean.setDistrict(district);
        robotsList.add(robotBean);

        return new MyResponse(robotsList, position, district);
    }

    public static String selectDistrict(ArrayList<RobotBean> robotsList) {
        Map<String, Integer> districtCounts = new HashMap<>();
        districtCounts.put("district1", 0);
        districtCounts.put("district2", 0);
        districtCounts.put("district3", 0);
        districtCounts.put("district4", 0);

        for (RobotBean robot : robotsList) {
            String district = robot.getDistrict();
            districtCounts.put(district, districtCounts.get(district) + 1);
        }

        int minCount = Collections.min(districtCounts.values());
        List<String> districtsWithMinCount = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : districtCounts.entrySet()) {
            if (entry.getValue() == minCount) {
                districtsWithMinCount.add(entry.getKey());
            }
        }

        if (districtsWithMinCount.size() == 4) {
            Random random = new Random();
            int index = random.nextInt(districtsWithMinCount.size());
            return districtsWithMinCount.get(index);
        }

        return districtsWithMinCount.get(0);
    }

    private int[] getPositionInDistrict(String district) {
        Map<String, int[]> districtLimits = new HashMap<>();
        districtLimits.put("district1", new int[]{0, 0, 4, 4});
        districtLimits.put("district2", new int[]{0, 5, 4, 9});
        districtLimits.put("district3", new int[]{5, 5, 9, 9});
        districtLimits.put("district4", new int[]{5, 0, 9, 4});

        int[] limits = districtLimits.getOrDefault(district, new int[]{0, 0, 9, 9});
        int minX = limits[0];
        int minY = limits[1];
        int maxX = limits[2];
        int maxY = limits[3];

        Random random = new Random();
        int x = random.nextInt(maxX - minX + 1) + minX;
        int y = random.nextInt(maxY - minY + 1) + minY;

        return new int[]{x, y};
    }

    public synchronized RobotBean getById(int id){
        RobotBean result = null;
        for (RobotBean robot : this.getRobotsList()) {
            if (id == robot.getId()) {
                result = robot;
                break;
            }
        }
        return result;
    }

    public synchronized RobotBean deleteById(int id) {
        for (int i = 0; i < robotsList.size(); i++) {
            RobotBean robotBean = robotsList.get(i);
            if (robotBean.getId() == id) {
                robotsList.remove(i);
                return robotBean;
            }
        }
        return null;
    }
}
