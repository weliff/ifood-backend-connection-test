package br.com.ifood.ifoodconnection.mqtthandler;

import br.com.ifood.ifoodconnection.model.dto.RestaurantStateDTO;
import br.com.ifood.ifoodconnection.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantConnectionMqttHandlerTest {

    @Mock
    private RestaurantService restaurantServiceMock;

    @Spy
    private ObjectMapper objectMapperSpy;

    @InjectMocks
    private RestaurantConnectionMqttHandler restaurantConnectionHandler;

    @Test
    public void shouldCallServiceUpdateStatusWhenReceiveMessage() throws Exception {
        RestaurantStateDTO dto = new RestaurantStateDTO(1L, true);
        String dtoStr = objectMapperSpy.writeValueAsString(dto);
        Message message = new GenericMessage<>(dtoStr);

        restaurantConnectionHandler.handle().handleMessage(message);

        Mockito.verify(objectMapperSpy).readValue(message.getPayload().toString(), RestaurantStateDTO.class);
        Mockito.verify(restaurantServiceMock).updateConnectionSate(dto);

    }
}