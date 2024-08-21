package RestServer.beans.robots;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * MyResponse represents a response containing a list of robots,
 * their position, and the associated district.
 */
@XmlRootElement
public class MyResponse {

    private ArrayList<RobotBean> robotsList;
    private int[] position;
    private String district;

    /** Default constructor. */
    public MyResponse(){}

    public MyResponse(ArrayList<RobotBean> robotsList, int[] position, String district) {
        this.robotsList = robotsList;
        this.position = position;
        this.district = district;
    }

    public ArrayList<RobotBean> getRobotsList() { return robotsList; }
    public void setRobotsList(ArrayList<RobotBean> robotsList) { this.robotsList = robotsList; }

    public int[] getPosition() { return position; }
    public void setPosition(int[] position) { this.position = position; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
}
