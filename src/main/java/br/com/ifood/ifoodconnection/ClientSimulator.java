package br.com.ifood.ifoodconnection;

import br.com.ifood.ifoodconnection.model.ConnectionState;
import br.com.ifood.ifoodconnection.model.dto.RestaurantStateDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ClientSimulator {

    public static void main(String[] args) throws MqttException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        RestaurantStateDTO restaurantConnectionOnline = new RestaurantStateDTO(1L, ConnectionState.ONLINE);
        byte[] onlineMessage = objectMapper.writeValueAsBytes(restaurantConnectionOnline);

        RestaurantStateDTO restaurantConnectionOffline = new RestaurantStateDTO(1L, ConnectionState.OFFLINE);
        byte[] offlineMessage = objectMapper.writeValueAsBytes(restaurantConnectionOffline);


        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        mqttConnectOptions.setKeepAliveInterval(120);

        mqttConnectOptions.setWill("connectionLost", offlineMessage, 1, false);

        client.connect(mqttConnectOptions);
        MqttMessage message = new MqttMessage();
        message.setPayload(onlineMessage);
        message.setQos(1);

        client.publish("connectionActive", message);

    }
}
