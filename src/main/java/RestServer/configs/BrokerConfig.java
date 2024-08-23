package RestServer.configs;

public class BrokerConfig {
    public static final String BROKER_ADDRESS = "tcp://localhost:1883";
    public static final String TOPIC = "greenfield/#";
    public static final String TOPIC_POLLUTION = "greenfield/pollution/";
    public static final int QOS = 2;

    // MESSAGES
    public static final String BROKER_CONNECTION_LOST_MESSAGE = "Connection lost! cause: ";
    public static final String BROKER_SUBSCRIPTION_MESSAGE = "[MQTT CLIENT] Subscribed to topic: ";
}