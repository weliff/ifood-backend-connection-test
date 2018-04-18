package br.com.ifood.ifoodconnection.service.stauts;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
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

import static br.com.ifood.ifoodconnection.model.ScheduleUnavailableReason.HOLIDAYS;
import static java.time.LocalDateTime.now;

@RunWith(MockitoJUnitRunner.class)
public class StatusChangeAvailableHandlerTest {

    @Mock
    private RestaurantRepository restaurantRepositoryMock;

    @InjectMocks
    private StatusChangeAvailableHandler availableHandler;

    @Test
    public void shouldChangeRestaurantStatusToAvailable() throws Exception {
        Long restaurantId = 1L;
        LocalDateTime endDate = now();
        RestaurantChangeStatusEvent event = new RestaurantChangeStatusEvent(restaurantId, RestaurantStatus.AVAILABLE, endDate);
        Restaurant restaurantFake = new Restaurant("Restaurant Fake", null);

        Mockito.when(restaurantRepositoryMock.findScheduleUnavailableByRestaurantAndEndDate(restaurantId, endDate))
                .thenReturn(Optional.of(new ScheduleUnavailable(1L, now(), endDate, restaurantFake, HOLIDAYS)));
        Mockito.when(restaurantRepositoryMock.save(restaurantFake)).thenReturn(restaurantFake);

        Optional<Restaurant> restaurantResult = availableHandler.apply(event);

        Mockito.verify(restaurantRepositoryMock).save(restaurantFake);
        Assertions.assertThat(restaurantResult)
                .isPresent();
    }

    @Test
    public void shouldNotApplyStatusAndReturnNothingWhenNotFoundSchedule() throws Exception {
        Long restaurantId = 1L;
        LocalDateTime endDate = now();
        RestaurantChangeStatusEvent event = new RestaurantChangeStatusEvent(restaurantId, RestaurantStatus.AVAILABLE, endDate);

        Mockito.when(restaurantRepositoryMock.findScheduleUnavailableByRestaurantAndEndDate(restaurantId, endDate))
                .thenReturn(Optional.empty());

        Optional<Restaurant> restaurantResult = availableHandler.apply(event);

        Mockito.verify(restaurantRepositoryMock, Mockito.never()).save(Mockito.any());
        Assertions.assertThat(restaurantResult)
                .isEmpty();
    }
}