package br.com.ifood.ifoodconnection.service.stauts;

import br.com.ifood.ifoodconnection.model.OpeningHour;
import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.publisher.PublisherChangeRestaurantStatus;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.ifood.ifoodconnection.model.ScheduleUnavailableReason.CONNECTION_ISSUES;
import static java.time.LocalDateTime.now;

@RunWith(MockitoJUnitRunner.class)
public class StatusChangeUnavailableHandlerTest {

    @Mock
    private RestaurantRepository restaurantRepositoryMock;

    @Mock
    private PublisherChangeRestaurantStatus publisherMock;

    @InjectMocks
    private StatusChangeUnavailableHandler unavailableHandler;

    @Test
    public void shouldChangeRestaurantStatusToAvailable() throws Exception {
        Long restaurantId = 1L;
        LocalDateTime endDate = now();
        RestaurantChangeStatusEvent event = new RestaurantChangeStatusEvent(restaurantId, RestaurantStatus.AVAILABLE, endDate);
        Restaurant restaurantFake = new Restaurant("Restaurant Fake", null);

        Mockito.when(restaurantRepositoryMock.findScheduleUnavailableByRestaurantAndStartDate(restaurantId, endDate))
                .thenReturn(Optional.of(new ScheduleUnavailable(1L, now(), endDate, restaurantFake, CONNECTION_ISSUES)));
        Mockito.when(restaurantRepositoryMock.save(restaurantFake)).thenReturn(restaurantFake);

        Optional<Restaurant> restaurantResult = unavailableHandler.apply(event);

        Mockito.verify(restaurantRepositoryMock).save(restaurantFake);
        Assertions.assertThat(restaurantResult)
                .isPresent();
    }

    @Test
    public void shouldReturnNothingWhenNotFoundSchedule() throws Exception {
        Long restaurantId = 1L;
        LocalDateTime endDate = now();
        RestaurantChangeStatusEvent event = new RestaurantChangeStatusEvent(restaurantId, RestaurantStatus.AVAILABLE, endDate);

        Mockito.when(restaurantRepositoryMock.findScheduleUnavailableByRestaurantAndStartDate(restaurantId, endDate))
                .thenReturn(Optional.empty());

        Optional<Restaurant> restaurantResult = unavailableHandler.apply(event);

        Assertions.assertThat(restaurantResult)
                .isEmpty();
    }

}