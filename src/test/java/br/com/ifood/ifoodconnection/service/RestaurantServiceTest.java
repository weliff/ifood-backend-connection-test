package br.com.ifood.ifoodconnection.service;

import br.com.ifood.ifoodconnection.model.ConnectionState;
import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import br.com.ifood.ifoodconnection.model.dto.RestaurantStateDTO;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.publisher.PublisherChangeRestaurantStatus;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import br.com.ifood.ifoodconnection.service.exception.RestaurantNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.ifood.ifoodconnection.model.ScheduleUnavailableReason.CONNECTION_ISSUES;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepositoryMock;

    @Mock
    private PublisherChangeRestaurantStatus publisherChangeRestaurantStatusMock;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test(expected = RestaurantNotFoundException.class)
    public void shouldThrowErrorWhenNotFoundRestaurantToSaveSchedule() throws Exception {
        Mockito.when(restaurantRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, LocalDateTime.now(), LocalDateTime.now(), CONNECTION_ISSUES);
        restaurantService.saveScheduleUnavailable(1L, scheduleUnavailable);
    }

    @Test
    public void shouldFindRestaurantAndSaveNewScheduleUnavailable() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant Fake");
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, LocalDateTime.now(), LocalDateTime.now(), CONNECTION_ISSUES);
        Mockito.when(restaurantRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.of(restaurant));

        restaurantService.saveScheduleUnavailable(1L, scheduleUnavailable);

        Mockito.verify(publisherChangeRestaurantStatusMock).publish(Mockito.any(RestaurantChangeStatusEvent.class));
        Mockito.verify(restaurantRepositoryMock).save(restaurant);

        assertThat(restaurant.getUnavailables())
                .contains(scheduleUnavailable)
                .hasSize(1);
    }

    @Test
    public void shouldFindRestaurantAndDeleteScheduleUnavailable() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant Fake");
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, LocalDateTime.now(), LocalDateTime.now(), CONNECTION_ISSUES);
        restaurant.addScheduleUnavailable(scheduleUnavailable);
        Mockito.when(restaurantRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.of(restaurant));

        restaurantService.deleteScheduleUnavailable(1L, 1L);

        Mockito.verify(restaurantRepositoryMock).save(restaurant);

        assertThat(restaurant.getUnavailables())
                .isEmpty();
    }

    @Test
    public void shouldUpdateConnectionState() throws Exception {
        Restaurant restaurant = new Restaurant("Restaurant Fake");
        Mockito.when(restaurantRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.of(restaurant));

        RestaurantStateDTO dto = new RestaurantStateDTO(1L, ConnectionState.ONLINE);

        restaurantService.updateConnectionSate(dto);

        Mockito.verify(restaurantRepositoryMock).save(restaurant);

        assertThat(restaurant.getConnectionState())
                .isEqualTo(ConnectionState.ONLINE);
    }
}