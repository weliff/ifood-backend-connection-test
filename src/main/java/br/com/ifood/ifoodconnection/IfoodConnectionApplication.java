package br.com.ifood.ifoodconnection;

import br.com.ifood.ifoodconnection.config.IfoodMqttProperty;
import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.dto.RestaurantStateDTO;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import br.com.ifood.ifoodconnection.service.exception.RestaurantNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication
public class IfoodConnectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfoodConnectionApplication.class, args);
    }

    // for simulate clients
    @RestController
    @RequestMapping("/clients/{restaurantId}")
    @Profile("!test")
    class RestaurantClientController {

        @Autowired
        private RestaurantRepository restaurantRepository;

        @Autowired
        private IfoodMqttProperty mqttProperty;

        private List<RestaurantClient> restaurantClients;

        @PostConstruct
        public void initClients() {
            this.restaurantClients = this.restaurantRepository.findAll()
                    .stream()
                    .map(r -> new RestaurantClient(r, mqttProperty))
                    .collect(Collectors.toList());
        }

        @GetMapping("/connect")
        public void connect(@PathVariable Long restaurantId) {
            RestaurantClient restaurantClient = restaurantClients.stream()
                    .filter(c -> Objects.equals(c.getRestaurant().getId(), restaurantId))
                    .findAny()
                    .orElseThrow(() -> new RestaurantNotFoundException("Restaurant client not found"));
            restaurantClient.connect();
        }


        @GetMapping("/disconnect")
        public void disconnect(@PathVariable Long restaurantId) {
            RestaurantClient restaurantClient = restaurantClients.stream()
                    .filter(c -> Objects.equals(c.getRestaurant().getId(), restaurantId))
                    .findAny()
                    .orElseThrow(() -> new RestaurantNotFoundException("Restaurant client not found"));
            restaurantClient.disconnect();
        }
    }

    @Data
    class RestaurantClient {

        private final IfoodMqttProperty mqttProperty;

        private MqttClient mqttClient;

        private Restaurant restaurant;

        private ObjectMapper objectMapper = new ObjectMapper();

        public RestaurantClient(Restaurant restaurant, IfoodMqttProperty mqttProperty) {
            this.restaurant = restaurant;
            this.mqttProperty = mqttProperty;
            try {
                this.mqttClient = new MqttClient(mqttProperty.getUrl(), restaurant.getName() + restaurant.getId());
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }

        public void connect() {
            try {
                mqttClient.connect(connectionOptions());
                MqttMessage message = new MqttMessage();
                message.setPayload(onlineMessage());
                message.setQos(MqttQoS.AT_LEAST_ONCE.value());
                mqttClient.publish("connectionActive", message);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }

        public void disconnect() {
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(offlineMessage());
                message.setQos(MqttQoS.AT_LEAST_ONCE.value());
                mqttClient.publish("connectionLost", message);
                this.mqttClient.disconnect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }

        private MqttConnectOptions connectionOptions() {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setKeepAliveInterval(120);//dois minutos
            mqttConnectOptions.setWill("connectionLost", offlineMessage(), MqttQoS.AT_LEAST_ONCE.value(), false);
            return mqttConnectOptions;
        }

        byte[] onlineMessage() {
            try {
                RestaurantStateDTO restaurantConnectionOffline = new RestaurantStateDTO(restaurant.getId(), true);
                return objectMapper.writeValueAsBytes(restaurantConnectionOffline);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }

        byte[] offlineMessage() {
            try {
                RestaurantStateDTO restaurantConnectionOffline = new RestaurantStateDTO(restaurant.getId(), false);
                return objectMapper.writeValueAsBytes(restaurantConnectionOffline);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
