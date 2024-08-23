package Robot.threads;

import RestServer.configs.BrokerConfig;
import Robot.Robot;
import Robot.utilities.MeasurementWrapper;
import Simulators.Measurement;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.util.List;

import static RestServer.threads.SubscribingThread.handleMqttException;

/**
 * The {@code PublisherThread} class is responsible for publishing sensor measurements from a robot
 * to an MQTT broker. This class extends {@link Thread} and overrides the {@code start()} method
 * to establish an MQTT connection and publish the data to a specific topic.
 *
 * <p>The measurements are wrapped in a {@code MeasurementWrapper} object, converted to JSON format,
 * and then sent as an MQTT message to the broker. The MQTT connection is configured with a clean session.</p>
 *
 * @see MqttClient
 * @see MqttMessage
 * @see MeasurementWrapper
 */

public class PublisherThread extends Thread {
    private final Robot robot;
    private final List<Measurement> measurement;
    private final long currentTimestamp;

    public PublisherThread(Robot robot, List<Measurement> measurement, long currentTimestamp){
        this.robot = robot;
        this.measurement = measurement;
        this.currentTimestamp = currentTimestamp;
    }

    /**
     * Starts the thread, generating a client ID, establishing an MQTT connection,
     * and publishing the measurements to the specified topic.
     */
    public void start() {
        String clientId = MqttClient.generateClientId();
        String topic = String.format("%s%s", BrokerConfig.TOPIC_POLLUTION, robot.getDistrict());

        try{
            MqttClient client = new MqttClient(BrokerConfig.BROKER_ADDRESS, clientId);
            connectMqttClient(client);
            publishMeasurements(client, topic);

        } catch (MqttException exception) {
            handleMqttException(exception);
        }
    }

    private void connectMqttClient(MqttClient client) throws MqttException {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        client.connect(connOpts);
    }

    /**
     * Publishes the measurements to the specified MQTT topic. The measurements are serialized to JSON
     * and sent as the payload of an MQTT message with QoS level 2.
     *
     * @param client the MQTT client used to publish the message
     * @param topic the topic to which the measurements are published
     * @throws MqttException if an error occurs during the publishing process
     */
    private void publishMeasurements(MqttClient client, String topic) throws MqttException {
        Gson gson = new Gson();
        MeasurementWrapper bundle = new MeasurementWrapper(robot.getId(), measurement, currentTimestamp);
        String payload = gson.toJson(bundle);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(BrokerConfig.QOS);
        client.publish(topic, message);
    }
}
