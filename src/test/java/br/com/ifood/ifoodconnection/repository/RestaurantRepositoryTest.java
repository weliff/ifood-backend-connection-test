package br.com.ifood.ifoodconnection.repository;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantHistory;
import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Restaurant restaurant = new Restaurant("Restaurant fake", null);
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
        Restaurant restaurant = new Restaurant("Restaurant fake", null);
        LocalDateTime scheduleEndDate = LocalDateTime.now().plusDays(10);
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now().plusDays(1), scheduleEndDate, HOLIDAYS));
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2), HOLIDAYS));

        restaurant = restaurantRepository.saveAndFlush(restaurant);
        Optional<ScheduleUnavailable> found = restaurantRepository.findScheduleUnavailableByRestaurantAndEndDate(restaurant.getId(), scheduleEndDate);

        Assertions.assertThat(found)
                .isPresent();
    }

    @Test
    public void shouldFindHistoryByRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant fake", null);
        restaurant.changeStatus(RestaurantStatus.UNAVAILABLE);
        Pageable pageable = PageRequest.of(0, 10);

        restaurant = restaurantRepository.saveAndFlush(restaurant);

        Page<RestaurantHistory> history = restaurantRepository.findHistory(restaurant.getId(), pageable);

        Assertions.assertThat(history.getTotalElements()).isEqualTo(1L);
        Assertions.assertThat(history.getTotalPages()).isEqualTo(1L);
    }

    @Test
    public void shouldFindAllSchedulesByRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant fake", null);
        LocalDateTime scheduleEndDate = LocalDateTime.now().plusDays(10);
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now().plusDays(1), scheduleEndDate, HOLIDAYS));
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2), HOLIDAYS));

        Pageable pageable = PageRequest.of(0, 15);

        restaurant = restaurantRepository.saveAndFlush(restaurant);
        Page<ScheduleUnavailable> schedules = restaurantRepository.findSchedulesUnavailableByRestaurant(restaurant.getId(), pageable);

        Assertions.assertThat(schedules.getTotalElements()).isEqualTo(2L);
        Assertions.assertThat(schedules.getTotalPages()).isEqualTo(1L);
    }
}
