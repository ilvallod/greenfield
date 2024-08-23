package RestServer;

import RestServer.beans.robots.RobotBean;
import RestServer.configs.ServerConfig;
import Robot.utilities.Colors;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.Scanner;

/**
 * The AdminClient class is a command-line application that allows administrators
 * to interact with a server to retrieve information about robots and air pollution statistics.
 * It provides several functionalities such as getting a list of robots, fetching robot statistics,
 * and calculating average air pollution within a specified time range.
 */
public class AdminClient {
    private static final Client CLIENT = Client.create();
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Entry point of the AdminClient application.
     * It provides a menu-driven interface for the user to choose different commands.
     */
    public static void main(String[] args){
        System.out.println(Colors.ANSI_GREEN + ServerConfig.ADMIN_CLIENT_INTRO + Colors.ANSI_RESET);

        int command = 0;
        boolean exit = false;
        while (!exit) {
            System.out.print(ServerConfig.ADMIN_CLIENT_COMMAND_LIST);
            try{
                command = scanner.nextInt();
                switch (command) {
                    case 1: getRobots(); break;
                    case 2: getRobotStatistics(); break;
                    case 3: getAverageAirPollution(); break;
                    case 4: exit = true; break;
                    default:
                        System.out.println(
                                Colors.ANSI_GRAY + ServerConfig.ADMIN_CLIENT_COMMAND_ERROR + Colors.ANSI_RESET);
                }
            } catch (Exception e){
                command = 0;
                System.out.println(ServerConfig.ADMIN_CLIENT_COMMAND_ERROR);
                scanner.nextLine(); // clear scanner buffer
            }
        }
        scanner.close();
        System.exit(0);
    }

    private static void getRobots(){
        WebResource webResource = CLIENT.resource(ServerConfig.ADDRESS_ROBOTS + "get");
        ClientResponse response = webResource.type("application/json").get(ClientResponse.class);

        try {
            String jsonResponse = response.getEntity(String.class);
            Gson gson = new Gson();
            RobotBean[] robots = gson.fromJson(jsonResponse, RobotBean[].class);

            if (robots.length > 0) {
                printRobotList(robots);
            } else {
                System.out.println("No robots found");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void printRobotList(RobotBean[] robots) {
        System.out.println("Robots in Greenfield: ");
        for (int i = 0; i < robots.length; i++) {
            RobotBean robot = robots[i];
            System.out.printf("%d. Robot %d:\n\t- IP: %s\n\t- Listening Port: %d\n\t- Position: (%d,%d)\n\n",
                    i + 1, robot.getId(), robot.getIp(), robot.getListeningPort(),
                    robot.getPosition()[0], robot.getPosition()[1]);
        }
    }

    /**
     * Retrieves and displays the average of a specified number of recent statistics for a particular robot.
     * The user is prompted to enter the robot ID and the number of statistics to average.
     */
    private static void getRobotStatistics(){
        System.out.print(ServerConfig.ADMIN_CLIENT_INPUT_ROBOT_ID);
        int id = scanner.nextInt();

        System.out.print(ServerConfig.ADMIN_CLIENT_INPUT_STATISTICS);
        int numberOfStatistics = scanner.nextInt();

        String statisticsUrl = ServerConfig.ADDRESS_STATISTICS + "get/" + id + "/" + numberOfStatistics;
        WebResource webResource = CLIENT.resource(statisticsUrl);
        ClientResponse response = webResource.type("application/json").get(ClientResponse.class);

        if (response.getStatus() == ServerConfig.HTTP_OK) {
            double value = Double.parseDouble(response.getEntity(String.class));
            System.out.printf(
                    Colors.ANSI_GREEN+"\nAverage of last %d statistics: %f\n"+Colors.ANSI_RESET,
                    numberOfStatistics, value);
        } else {
            System.out.println(ServerConfig.ADMIN_CLIENT_FAILED_STATISTICS + response.getStatus());
        }
    }

    /**
     * Calculates and displays the average air pollution between two timestamps.
     * The user is prompted to enter the start and end timestamps.
     */
    private static void getAverageAirPollution(){
        System.out.print(ServerConfig.ADMIN_CLIENT_INPUT_FIRST_TIMESTAMP);
        long ts1 = scanner.nextLong();

        System.out.print(ServerConfig.ADMIN_CLIENT_INPUT_SECOND_TIMESTAMP);
        long ts2 = scanner.nextLong();

        String statisticsUrl = ServerConfig.ADDRESS_STATISTICS + "get/" + ts1 + "-" + ts2;
        WebResource webResource = CLIENT.resource(statisticsUrl);
        ClientResponse response = webResource.type("application/json").get(ClientResponse.class);

        if (response.getStatus() == ServerConfig.HTTP_OK) {
            String pollutionData = response.getEntity(String.class);
            System.out.printf(
                    Colors.ANSI_GREEN+"\nAverage pollution between %d and %d: %s\n"+Colors.ANSI_RESET,
                    ts1, ts2, pollutionData);
        } else {
            System.out.println(ServerConfig.ADMIN_CLIENT_FAILED_POLLUTION + response.getStatus());
        }
    }
}
