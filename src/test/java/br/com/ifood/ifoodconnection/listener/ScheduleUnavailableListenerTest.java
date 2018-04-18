package br.com.ifood.ifoodconnection.listener;

import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleUnavailableListenerTest {

    @Mock
    private RestaurantService restaurantServiceMock;

    @Spy
    private ObjectMapper objectMapperSpy;

    @InjectMocks
    private ScheduleUnavailableListener scheduleUnavailableListener;

    @Test
    public void shouldCallServiceUpdateStatusWhenReceiveMessage() throws Exception {
        RestaurantChangeStatusEvent event = new RestaurantChangeStatusEvent(1L, RestaurantStatus.AVAILABLE, null);
        String eventString = objectMapperSpy.writeValueAsString(event);

        scheduleUnavailableListener.onRestaurantStatusChange(eventString);

        Mockito.verify(objectMapperSpy).readValue(eventString, RestaurantChangeStatusEvent.class);
        Mockito.verify(restaurantServiceMock).updateRestaurantStatusByEvent(event);

    }

}