package br.com.ifood.ifoodconnection;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.stream.IntStream;

@SpringBootApplication
public class IfoodConnectionApplication {

	@Value("${ifood.connection.minutes-await-ping}")
	private Integer minutesAwaitPing;

	public static void main(String[] args) {
		SpringApplication.run(IfoodConnectionApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(RestaurantRepository restaurantRepository) {
		return args ->
			IntStream.range(1, 10)
				.forEach(i -> {
					Restaurant restaurant = new Restaurant("Restaurant " + i, this.minutesAwaitPing);
					restaurantRepository.save(restaurant);
				});
	}
}
