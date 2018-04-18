package br.com.ifood.ifoodconnection.publisher;

import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;

public interface PublisherChangeRestaurantStatus {

    void publish(RestaurantChangeStatusEvent restaurantChangeStatusEvent);
}
