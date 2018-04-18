package br.com.ifood.ifoodconnection.service.stauts;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;

import java.util.Optional;

public class StatusChangeAvailableHandler implements StatusChangeEventHandler {

    private RestaurantRepository restaurantRepository;

    public StatusChangeAvailableHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Optional<Restaurant> apply(RestaurantChangeStatusEvent event) {
        return restaurantRepository.findScheduleUnavailableByRestaurantAndEndDate(event.getRestaurantId(), event.getDate())
            .map(scheduleUnavailable -> {
                Restaurant restaurant = scheduleUnavailable.getRestaurant();
                restaurant.changeStatus(event.getStatus());
                restaurant = restaurantRepository.save(restaurant);
                return restaurant;
            });
    }
}
