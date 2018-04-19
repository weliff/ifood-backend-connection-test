package br.com.ifood.ifoodconnection;

import br.com.ifood.ifoodconnection.model.OpeningHour;
import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalTime;
import java.util.stream.IntStream;

@SpringBootApplication
public class IfoodConnectionApplication {

	@Value("${ifood.connection.minutes-await-ping}")
	private Integer minutesAwaitPing;

	public static void main(String[] args) {
		SpringApplication.run(IfoodConnectionApplication.class, args);
	}

	// for simulate clients
	@Bean
	ApplicationRunner applicationRunner(RestaurantRepository restaurantRepository) {
		return args -> {
			IntStream.range(1, 10)
					.forEach(i -> {
						OpeningHour openingHour = new OpeningHour(LocalTime.of(10, 0), LocalTime.of(23, 59));
						Restaurant restaurant = new Restaurant("Restaurant " + i, openingHour);
						restaurantRepository.save(restaurant);
					});

            ObjectMapper objectMapper = new ObjectMapper();

//            restaurantRepository.findAll().forEach(restaurant -> {
//                try {
//
//                    RestaurantStateDTO restaurantConnectionOnline = new RestaurantStateDTO(restaurant.getId(), ConnectionState.ONLINE);
//                    byte[] onlineMessage = objectMapper.writeValueAsBytes(restaurantConnectionOnline);
//
//                    RestaurantStateDTO restaurantConnectionOffline = new RestaurantStateDTO(restaurant.getId(), ConnectionState.OFFLINE);
//                    byte[] offlineMessage = objectMapper.writeValueAsBytes(restaurantConnectionOffline);
//
//                    MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
//                    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//
//                    mqttConnectOptions.setKeepAliveInterval(120);
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
