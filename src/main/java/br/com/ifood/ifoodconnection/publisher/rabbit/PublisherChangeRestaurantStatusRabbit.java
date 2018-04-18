package br.com.ifood.ifoodconnection.publisher.rabbit;

import br.com.ifood.ifoodconnection.config.Exchanges;
import br.com.ifood.ifoodconnection.config.Queues;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.publisher.PublisherChangeRestaurantStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PublisherChangeRestaurantStatusRabbit implements PublisherChangeRestaurantStatus {

    private RabbitMessagingTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public PublisherChangeRestaurantStatusRabbit(RabbitMessagingTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(RestaurantChangeStatusEvent event) {
        try {
            String eventString = objectMapper.writeValueAsString(event);
            //TODO: remove
//            long delayToPublish = ChronoUnit.MILLIS.between(LocalDateTime.getNow(), event.getDate());
            long delayToPublish = 1000L;

            rabbitTemplate.convertAndSend(Exchanges.RESTAURANT_STATUS_CHANGE,
                    Queues.RESTAURANT_STATUS_CHANGE, eventString , Map.of("x-delay", delayToPublish));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
