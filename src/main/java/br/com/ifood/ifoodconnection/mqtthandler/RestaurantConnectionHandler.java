package br.com.ifood.ifoodconnection.mqtthandler;

import br.com.ifood.ifoodconnection.model.dto.RestaurantStateDTO;
import br.com.ifood.ifoodconnection.model.exception.RestaurantIsNotOpenNowException;
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
                RestaurantStateDTO connectionState = objectMapper.readValue(message.getPayload().toString(), RestaurantStateDTO.class);
                restaurantService.updateConnectionSate(connectionState);
            }catch (RestaurantIsNotOpenNowException e) {
                log.warn(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException("Error reading RestaurantStateDTO!", e);
            }
        };
    }
}
