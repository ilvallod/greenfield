package RestServer.configs;

public class ServerConfig {
    //TODO: lessicographical order
    //TODO: all English statement
    public static final int PORT = 1337;
    public static final String ADDRESS = String.format("http://localhost:%d/", PORT);

    // ENDPOINTS
    public static final String ADDRESS_STATISTICS_ADD = String.format("%s/statistics/add", ADDRESS);
    public static final String ADDRESS_ROBOTS_ADD = String.format("%s/robots/add", ADDRESS);
    public static final String ADDRESS_ROBOTS_REMOVE = String.format("%s/robots/remove", ADDRESS);

    // MESSAGES
    public static final String START_MESSAGE = "Server started on: ";
    public static final String RETURN_TO_STOP_MESSAGE = "Hit return to stop...";
    public static final String STOP_START_MESSAGE = "Stopping server";
    public static final String STOP_END_MESSAGE = "Server stopped";
    public static final String UNAVAILABLE_MESSAGE = "Server non disponibile";
    public static final String ROBOT_ADDED_MESSAGE = "Robot added! Starting position ";
    public static final String ROBOT_LIST_MESSAGE = "Robot already in the system:";
    public static final String ROBOT_CONFLICT_MESSAGE = "Robot with this ID/port already in the system";
    public static final String UNEXPECTED_RESPONSE_MESSAGE = "Unexpected response from server: ";
    public static final String FAILED_RESPONSE_MESSAGE = "Failed to receive response from server";

    // STATUS
    public static final int HTTP_OK = 200;
    public static final int HTTP_CONFLICT = 409;
}