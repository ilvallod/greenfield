package RestServer.threads;

import RestServer.configs.BrokerConfig;
import RestServer.configs.ServerConfig;
import RestServer.beans.statistics.StatisticBean;
import Robot.utilities.Colors;
import Robot.utilities.MeasurementWrapper;
import Simulators.Measurement;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Robot.utilities.RestMethods.postRequest;

public class SubscribingThread extends Thread {
    private static final String CLIENT_ID = MqttClient.generateClientId();

    public void run() {
        try (MqttClient client = new MqttClient(BrokerConfig.BROKER_ADDRESS, CLIENT_ID)) {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);

            client.setCallback(new MqttCallback() {
                public void messageArrived(String topic, MqttMessage message) { handleReceivedMessage(message); }
                public void connectionLost(Throwable cause) { handleConnectionLost(cause); }
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            client.subscribe(BrokerConfig.TOPIC, BrokerConfig.QOS);
            String subscriptionMessage =
                    String.format("%n%n%s%s", BrokerConfig.BROKER_SUBSCRIPTION_MESSAGE, BrokerConfig.TOPIC);
            System.out.printf("%s%s%s", Colors.ANSI_GREEN, subscriptionMessage, Colors.ANSI_RESET);

        } catch (MqttException exception) {
            handleMqttException(exception);
        }
    }

    private static void handleReceivedMessage(MqttMessage message) {
        String receivedMessage = new String(message.getPayload());
        Gson gson = new Gson();
        MeasurementWrapper bundle = gson.fromJson(receivedMessage, MeasurementWrapper.class);

        List<Measurement> measurements = bundle.getMeasurements();
        List<Double> avgPollutionList = new ArrayList<>();
        for (Measurement measurement : measurements) {
            double value = measurement.getValue();
            avgPollutionList.add(value);
        }

        StatisticBean statisticBean =
                new StatisticBean(bundle.getRobotId(), avgPollutionList, System.currentTimeMillis());
        Client client = Client.create();

        postRequest(client, ServerConfig.ADDRESS_STATISTICS_ADD, statisticBean);
    }

    private static void handleConnectionLost(Throwable cause) {
        System.out.printf("%s%s%s - Thread: %d%n%s%n",
                CLIENT_ID, BrokerConfig.BROKER_CONNECTION_LOST_MESSAGE, cause.getMessage(),
                Thread.currentThread().getId(), Arrays.toString(cause.getStackTrace()));
    }

    public static void handleMqttException(MqttException exception) {
        System.out.printf("%nreason %d%nmsg %s%nloc %s%ncause %s%nexcep %s%n",
                exception.getReasonCode(), exception.getMessage(), exception.getLocalizedMessage(), exception.getCause(), exception);
    }
}

