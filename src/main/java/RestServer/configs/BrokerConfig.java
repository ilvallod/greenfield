package RestServer.configs;

public class BrokerConfig {
    public static final String BROKER_ADDRESS = "tcp://localhost:1883";
    public static final String TOPIC = "greenfield/#";
    public static final int QOS = 2;

    public static final String BROKER_SUBSCRIPTION_MESSAGE = "[MQTT CLIENT] Subscribed to topic: ";
    public static final String BROKER_CONNECTION_LOST_MESSAGE = "Connection lost! cause: ";
}