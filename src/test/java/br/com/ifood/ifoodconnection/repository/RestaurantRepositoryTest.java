package br.com.ifood.ifoodconnection.repository;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.ifood.ifoodconnection.model.ScheduleUnavailableReason.HOLIDAYS;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void shouldFindSpecificScheduleUnavailableByRestaurantAndStartDate() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant fake");
        LocalDateTime scheduleStartDate = LocalDateTime.now().plusDays(10);
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, scheduleStartDate, LocalDateTime.now().plusDays(20), HOLIDAYS));
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(3), HOLIDAYS));

        restaurant = restaurantRepository.saveAndFlush(restaurant);
        Optional<ScheduleUnavailable> found = restaurantRepository.findScheduleUnavailableByRestaurantAndStartDate(restaurant.getId(), scheduleStartDate);

        Assertions.assertThat(found)
                .isPresent();
    }

    @Test
    public void shouldFindRestaurantWithSpecificScheduleUnavailableByRestaurantAndEndDate() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant fake");
        LocalDateTime scheduleEndDate = LocalDateTime.now().plusDays(10);
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now(), scheduleEndDate, HOLIDAYS));
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(3), HOLIDAYS));

        restaurant = restaurantRepository.saveAndFlush(restaurant);
        Optional<ScheduleUnavailable> found = restaurantRepository.findScheduleUnavailableByRestaurantAndStartDate(restaurant.getId(), scheduleEndDate);

        Assertions.assertThat(found)
                .isPresent();
    }
}
