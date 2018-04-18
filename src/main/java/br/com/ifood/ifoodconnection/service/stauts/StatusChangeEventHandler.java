package br.com.ifood.ifoodconnection.service.stauts;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;

import java.util.Optional;

public interface StatusChangeEventHandler {

    Optional<Restaurant> apply(RestaurantChangeStatusEvent event);

}
