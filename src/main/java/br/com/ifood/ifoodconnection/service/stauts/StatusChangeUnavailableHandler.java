package br.com.ifood.ifoodconnection.service.stauts;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.publisher.PublisherChangeRestaurantStatus;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;

import java.util.Optional;

public class StatusChangeUnavailableHandler implements StatusChangeEventHandler {

    private RestaurantRepository restaurantRepository;

    private PublisherChangeRestaurantStatus publisherChangeStatus;

    public StatusChangeUnavailableHandler(RestaurantRepository restaurantRepository, PublisherChangeRestaurantStatus publisherChangeStatus) {
        this.restaurantRepository = restaurantRepository;
        this.publisherChangeStatus = publisherChangeStatus;
    }

    @Override
    public Optional<Restaurant> apply(RestaurantChangeStatusEvent event) {
        return restaurantRepository.findScheduleUnavailableByRestaurantAndStartDate(event.getRestaurantId(), event.getDate())
            .map(scheduleUnavailable -> {
                scheduleUnavailable.apply();
                Restaurant restaurant = scheduleUnavailable.getRestaurant();
                restaurant.changeStatus(RestaurantStatus.UNAVAILABLE);
                restaurant = restaurantRepository.save(restaurant);

                RestaurantChangeStatusEvent eventAvailable = new RestaurantChangeStatusEvent(event.getRestaurantId(),
                        RestaurantStatus.AVAILABLE, scheduleUnavailable.getEnd());
                publisherChangeStatus.publish(eventAvailable);
                return restaurant;
            });
    }
}
