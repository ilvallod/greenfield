package Robot.utilities;

import RestServer.beans.robots.MyResponse;
import RestServer.beans.robots.RobotBean;
import RestServer.beans.statistics.StatisticBean;
import RestServer.configs.ServerConfig;
import Robot.Robot;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class providing methods to handle RESTful requests related to robots and statistics.
 */
public class RestMethods {

    // ROBOTS //
    public static ClientResponse postRequest(Client client, String url, RobotBean robot){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(robot);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);

        } catch (ClientHandlerException e) {
            System.out.println(ServerConfig.UNAVAILABLE_MESSAGE);
            return null;
        }
    }

    public static ClientResponse deleteRequest(Client client, String url, int robotId) {
        WebResource webResource = client.resource(url + "/" + robotId);
        try {
            return webResource.type("application/json").delete(ClientResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Handles the insertion of a robot into the system by sending a POST request and processing the response.
     *
     * @param robot the Robot object to be inserted.
     * @return true if the insertion is successful, false otherwise.
     */
    public static boolean insertion(Robot robot){
        RobotBean robotBean = new RobotBean(robot.getId(), "localhost", robot.getPort());
        Client client = Client.create();
        ClientResponse clientResponse = postRequest(client, ServerConfig.ADDRESS_ROBOTS_ADD, robotBean);

        if (clientResponse != null) {
            int statusCode = clientResponse.getStatus();
            if (statusCode == ServerConfig.HTTP_OK) {
                MyResponse myResponse = clientResponse.getEntity(MyResponse.class);

                updateRobotBean(robotBean, myResponse);
                updateRobot(robot, myResponse);

                System.out.printf("%n%s%s:", ServerConfig.ROBOT_ADDED_MESSAGE, Arrays.toString(myResponse.getPosition()));
                System.out.printf("%n%s%n", ServerConfig.ROBOT_LIST_MESSAGE);
                printAsAList(myResponse.getRobotsList());
                return true;
            } else if (statusCode == ServerConfig.HTTP_CONFLICT) {
                System.err.println(ServerConfig.ROBOT_CONFLICT_MESSAGE);
                return false;
            } else {
                System.err.printf("%s%s", ServerConfig.UNEXPECTED_RESPONSE_MESSAGE, statusCode);
                return false;
            }
        } else {
            System.err.println(ServerConfig.FAILED_RESPONSE_MESSAGE);
            return false;
        }
    }

    /** Updates the RobotBean with the data received from the server. **/
    private static void updateRobotBean(RobotBean robotBean, MyResponse myResponse){
        robotBean.setPosition(myResponse.getPosition());
        robotBean.setDistrict(myResponse.getDistrict());
    }

    /** Updates the Robot object with the data received from the server. **/
    private static void updateRobot(Robot robot, MyResponse myResponse){
        robot.setPosition(myResponse.getPosition()); //set position for presentation
        robot.setDistrict(myResponse.getDistrict()); //set district for presentation
        robot.setRobotsBeanList(myResponse.getRobotsList());
        robot.setConnectedRobots(fromBeanToRobot(myResponse.getRobotsList()));
    }

    /** Converts a list of RobotBean objects to a list of Robot objects. **/
    public static ArrayList<Robot> fromBeanToRobot(ArrayList<RobotBean> robotsBeanList){
        ArrayList<Robot> robotsList = new ArrayList<>();
        for (RobotBean robotBean : robotsBeanList) {
            Robot robot = new Robot(robotBean.getId(), robotBean.getIp(), robotBean.getListeningPort());
            robotsList.add(robot);
        }
        return robotsList;
    }

    public static boolean removal(int robotId) {
        Client client = Client.create();
        ClientResponse response = deleteRequest(client, ServerConfig.ADDRESS_ROBOTS_REMOVE, robotId);
        return response != null && response.getStatus() == ServerConfig.HTTP_OK;
    }

    public static void printAsAList(ArrayList<RobotBean> robotsList) {
        for (int i = 0; i < robotsList.size(); i++) {
            RobotBean robot = robotsList.get(i);
            System.out.println((i+1)+". Robot: "
                    + "\n\t- id: " + robot.getId()
                    + "\n\t- ip: " + robot.getIp()
                    + "\n\t- port: " + robot.getListeningPort()
                    + "\n\t- position: " + Arrays.toString(robot.getPosition())
                    + '\n');
        }
    }

    // STATISTICS //
    public static ClientResponse postRequest(Client client, String url, StatisticBean statisticBean){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(statisticBean);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);

        } catch (ClientHandlerException e) {
            System.out.println(ServerConfig.UNAVAILABLE_MESSAGE);
            return null;
        }
    }
}
