package br.com.ifood.ifoodconnection.repository;

import br.com.ifood.ifoodconnection.fake.FakeOpeningHour;
import br.com.ifood.ifoodconnection.model.*;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static br.com.ifood.ifoodconnection.model.ScheduleUnavailableReason.HOLIDAYS;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant;

    private final LocalDateTime scheduleStartDate = LocalDateTime.now().plusDays(10);

    private final LocalDateTime scheduleEndDate = LocalDateTime.now().plusDays(3);

    @Before
    public void setUp() throws Exception {
        OpeningHour openingHour = FakeOpeningHour.openNow();
        Restaurant restaurant = new Restaurant("Restaurant fake", openingHour);
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, scheduleStartDate, LocalDateTime.now().plusDays(20), HOLIDAYS));
        restaurant.addScheduleUnavailable(new ScheduleUnavailable(null, LocalDateTime.now().plusMinutes(1), scheduleEndDate, HOLIDAYS));

        //to history
        restaurant.changeStatus(RestaurantStatus.AVAILABLE);

        this.restaurant = restaurantRepository.saveAndFlush(restaurant);
    }

    @Test
    public void shouldFindSpecificScheduleUnavailableByRestaurantAndStartDate() throws Exception {
        Optional<ScheduleUnavailable> found = restaurantRepository.findScheduleUnavailableByRestaurantAndStartDate(restaurant.getId(), scheduleStartDate);
        Assertions.assertThat(found)
                .isPresent();
    }

    @Test
    public void shouldFindRestaurantWithSpecificScheduleUnavailableByRestaurantAndEndDate() throws Exception {
        Optional<ScheduleUnavailable> found = restaurantRepository.findScheduleUnavailableByRestaurantAndEndDate(restaurant.getId(), scheduleEndDate);

        Assertions.assertThat(found)
                .isPresent();
    }

    @Test
    public void shouldFindHistoryByRestaurant() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        Page<RestaurantHistory> history = restaurantRepository.findHistory(restaurant.getId(), pageable);

        Assertions.assertThat(history.getTotalElements()).isEqualTo(1L);
        Assertions.assertThat(history.getTotalPages()).isEqualTo(1L);
    }

    @Test
    public void shouldFindAllSchedulesByRestaurant() throws Exception {
        Pageable pageable = PageRequest.of(0, 15);

        Page<ScheduleUnavailable> schedules = restaurantRepository.findSchedulesUnavailableByRestaurant(restaurant.getId(), pageable);

        Assertions.assertThat(schedules.getTotalElements()).isEqualTo(2L);
        Assertions.assertThat(schedules.getTotalPages()).isEqualTo(1L);
    }
}
