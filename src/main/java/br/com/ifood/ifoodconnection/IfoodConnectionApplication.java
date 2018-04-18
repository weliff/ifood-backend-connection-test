package br.com.ifood.ifoodconnection;

import br.com.ifood.ifoodconnection.model.ConnectionState;
import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantState;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.stream.CharacterStreamReadingMessageSource;
import org.springframework.messaging.MessageHandler;

import java.time.Duration;
import java.util.stream.IntStream;

@SpringBootApplication
public class IfoodConnectionApplication {

	@Value("${ifood.connection.minutes-await-ping}")
	private Integer minutesAwaitPing;

	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(IfoodConnectionApplication.class, args);
	}

	// for simulate clients
	@Bean
	ApplicationRunner applicationRunner(RestaurantRepository restaurantRepository) {
		return args -> {
			IntStream.range(1, 10)
					.forEach(i -> {
						Restaurant restaurant = new Restaurant("Restaurant " + i);
						restaurantRepository.save(restaurant);
					});

            ObjectMapper objectMapper = new ObjectMapper();

//            restaurantRepository.findAll().forEach(restaurant -> {
//                try {
//
//                    RestaurantState restaurantConnectionOnline = new RestaurantState(restaurant.getId(), ConnectionState.ONLINE);
//                    byte[] onlineMessage = objectMapper.writeValueAsBytes(restaurantConnectionOnline);
//
//                    RestaurantState restaurantConnectionOffline = new RestaurantState(restaurant.getId(), ConnectionState.OFFLINE);
//                    byte[] offlineMessage = objectMapper.writeValueAsBytes(restaurantConnectionOffline);
//
//                    MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
//                    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//
//                    mqttConnectOptions.setKeepAliveInterval(15);
//
//                    mqttConnectOptions.setWill("connectionLost", offlineMessage, MqttQoS.AT_LEAST_ONCE.value(), false);
//
//                    client.connect(mqttConnectOptions);
//                    MqttMessage message = new MqttMessage();
//                    message.setPayload(onlineMessage);
//                    message.setQos(MqttQoS.AT_LEAST_ONCE.value());
//
//                    client.publish("connectionActive", message);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            });
		};

	}

}
