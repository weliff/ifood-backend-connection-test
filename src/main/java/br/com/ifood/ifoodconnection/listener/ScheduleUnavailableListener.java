package br.com.ifood.ifoodconnection.listener;

import br.com.ifood.ifoodconnection.config.Queues;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScheduleUnavailableListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantService restaurantService;

    @RabbitListener(queues = Queues.RESTAURANT_STATUS_CHANGE)
    public void onRestaurantStatusChange(String message) {
        try {
            RestaurantChangeStatusEvent event = objectMapper.readValue(message, RestaurantChangeStatusEvent.class);
            restaurantService.updateRestaurantStatusByEvent(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
