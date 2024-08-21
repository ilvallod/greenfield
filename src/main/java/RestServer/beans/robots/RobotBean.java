package RestServer.beans.robots;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a robot with an ID, IP address, listening port, position, and district.
 * This class is used for storing and manipulating robot-related information.
 */
@XmlRootElement
public class RobotBean {
    private int id;
    private String ip;
    private int listeningPort;
    private int[] position;
    private String district;

    /** Default constructor. */
    public RobotBean(){}

    public RobotBean(int id, String ip, int listeningPort) {
        this.id = id;
        this.ip = ip;
        this.listeningPort = listeningPort;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public int getListeningPort() { return listeningPort; }
    public void setListeningPort(int listeningPort) { this.listeningPort = listeningPort; }

    public int[] getPosition() { return position; }
    public void setPosition(int[] position) { this.position = position; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
}