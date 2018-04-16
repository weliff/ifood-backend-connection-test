package br.com.ifood.ifoodconnection.handler;

import br.com.ifood.ifoodconnection.model.RestaurantState;
import br.com.ifood.ifoodconnection.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RestaurantConnectionHandler {

    private RestaurantService restaurantService;

    private ObjectMapper objectMapper;

    public RestaurantConnectionHandler(RestaurantService restaurantService, ObjectMapper objectMapper) {
        this.restaurantService = restaurantService;
        this.objectMapper = objectMapper;
    }

    public MessageHandler handle() {
        return message -> {
            try {
                RestaurantState connectionState = objectMapper.readValue(message.getPayload().toString(), RestaurantState.class);
                restaurantService.updateConnectionSate(connectionState);
            } catch (IOException e) {
                throw new RuntimeException("Error reading RestaurantState!", e);
            }
        };
    }
}
