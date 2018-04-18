package br.com.ifood.ifoodconnection.publisher.rabbit;

import br.com.ifood.ifoodconnection.config.Exchanges;
import br.com.ifood.ifoodconnection.config.Queues;
import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class PublisherChangeRestaurantStatusRabbitTest {

    @Mock
    private RabbitMessagingTemplate rabbitTemplateMock;

    @Spy
    private ObjectMapper objectMapperSpy;

    @InjectMocks
    private PublisherChangeRestaurantStatusRabbit publisherChangeStatusRabbit;

    @Test
    public void shouldSendMessageToRabbitWithDelayToPublish() throws Exception {
        RestaurantChangeStatusEvent event = new RestaurantChangeStatusEvent(1L, RestaurantStatus.UNAVAILABLE, LocalDateTime.now());
        String eventStr = objectMapperSpy.writeValueAsString(event);

        Mockito.when(objectMapperSpy.writeValueAsString(Mockito.eq(event))).thenReturn(eventStr);

        publisherChangeStatusRabbit.publish(event);

        Mockito.verify(rabbitTemplateMock).convertAndSend(eq(Exchanges.RESTAURANT_STATUS_CHANGE),
                eq(Queues.RESTAURANT_STATUS_CHANGE), eq(eventStr), Mockito.anyMap());
    }
}