package RestServer.configs;

public class ServerConfig {
    public static final int PORT = 1337;
    public static final String ADDRESS = String.format("http://localhost:%d/", PORT);

    // ENDPOINTS
    public static final String ADDRESS_ROBOTS_ADD = String.format("%s/robots/add", ADDRESS);
    public static final String ADDRESS_ROBOTS_REMOVE = String.format("%s/robots/remove", ADDRESS);
    public static final String ADDRESS_STATISTICS_ADD = String.format("%s/statistics/add", ADDRESS);

    // FIX MESSAGES
    public static final String FIX_REQUEST = "Fix request already started!";

    // MESSAGES
    public static final String FAILED_RESPONSE_MESSAGE = "Failed to receive response from server";
    public static final String RETURN_TO_STOP_MESSAGE = "Hit return to stop...";
    public static final String ROBOT_ADDED_MESSAGE = "Robot added! Starting position ";
    public static final String ROBOT_CONFLICT_MESSAGE = "Robot with this ID/port already in the system";
    public static final String ROBOT_LIST_MESSAGE = "Robot already in the system:";
    public static final String START_MESSAGE = "Server started on: ";
    public static final String STOP_END_MESSAGE = "Server stopped";
    public static final String STOP_START_MESSAGE = "Stopping server";
    public static final String UNAVAILABLE_MESSAGE = "Server unavailable";
    public static final String UNEXPECTED_RESPONSE_MESSAGE = "Unexpected response from server: ";

    // REPAIRING MESSAGE
    public static final String REPAIRING = "\n... Repairing ...\n";
    public static final String REPAIR_END = "\n[MUTEX] Robot completely repaired \n";
    public static final String REPAIR_START = "\nRobot can now repair\n";

    // STANDARD INPUT
    public static final String STDIN_FIX = "[STANDARD INPUT] Write \"fix\" to go to mechanic.";
    public static final String STDIN_QUIT = "[STANDARD INPUT] Write \"quit\" to delete robot.";

    // STATUS
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_OK = 200;

    // QUIT MESSAGES
    public static final String QUIT_END = "[QUIT] Robot ending work";
    public static final String QUIT_START = "[QUIT] Quit received from user";
}