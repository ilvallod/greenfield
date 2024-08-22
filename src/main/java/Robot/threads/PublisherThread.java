package Robot.threads;

import Robot.Robot;
import Robot.utilities.MeasurementWrapper;
import Simulators.Measurement;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.util.List;

import static RestServer.threads.SubscribingThread.handleMqttException;

public class PublisherThread extends Thread {
    private final Robot robot;
    private final List<Measurement> measurement;
    private final long currentTimestamp;


    public PublisherThread(Robot robot, List<Measurement> measurement, long currentTimestamp){
        this.robot = robot;
        this.measurement = measurement;
        this.currentTimestamp = currentTimestamp;
    }

    public void start(){
        String clientId = MqttClient.generateClientId();
        String topic = String.format("greenfield/pollution/%s", robot.getDistrict());
        String brokerAddress = "tcp://localhost:1883";

        try{
            MqttClient client = new MqttClient(brokerAddress, clientId);

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

    private void publishMeasurements(MqttClient client, String topic) throws MqttException {
        Gson gson = new Gson();
        MeasurementWrapper bundle = new MeasurementWrapper(robot.getId(), measurement, currentTimestamp);
        String payload = gson.toJson(bundle);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(2);

        client.publish(topic, message);
    }
}
