package br.com.ifood.ifoodconnection.service;

import br.com.ifood.ifoodconnection.model.*;
import br.com.ifood.ifoodconnection.model.dto.RestaurantStateDTO;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.publisher.PublisherChangeRestaurantStatus;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import br.com.ifood.ifoodconnection.service.exception.RestaurantNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RestaurantService {

    private RestaurantRepository restaurantRepository;

    private PublisherChangeRestaurantStatus publisherChangeRestaurantStatus;

    public RestaurantService(RestaurantRepository restaurantRepository, PublisherChangeRestaurantStatus publisherChangeRestaurantStatus) {

        this.restaurantRepository = restaurantRepository;
        this.publisherChangeRestaurantStatus = publisherChangeRestaurantStatus;
    }

    @CachePut(cacheNames = "restaurants", key = "#id")
    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(String.format("Not found the resource Restaurant with id=%s", id)));
    }

    @CachePut(cacheNames = "restaurants", key = "#restaurantId")
    @Transactional
    public Restaurant saveScheduleUnavailable(Long restaurantId, ScheduleUnavailable scheduleUnavailable) {
        log.info("Saving schedule unavailable for restaurantId={}, ScheduleUnavailable={}", restaurantId, scheduleUnavailable);
        Restaurant restaurant = findById(restaurantId);
        restaurant.addScheduleUnavailable(scheduleUnavailable);
        restaurant = restaurantRepository.save(restaurant);
        RestaurantChangeStatusEvent event = new RestaurantChangeStatusEvent(restaurantId, RestaurantStatus.UNAVAILABLE, scheduleUnavailable.getStart());
        publisherChangeRestaurantStatus.publish(event);
        return restaurant;
    }

    @CachePut(cacheNames = "restaurants", key = "#restaurantId")
    @Transactional
    public Restaurant deleteScheduleUnavailable(Long restaurantId, Long scheduleId) {
        log.info("Removing schedule for restaurantId={}, scheduleId={}", restaurantId, scheduleId);
        Restaurant restaurant = findById(restaurantId);
        restaurant.removeScheduleUnavailable(scheduleId);
        return restaurantRepository.save(restaurant);
    }

    @CachePut(cacheNames = "restaurants", key = "#event.getRestaurantId()")
    @Transactional
    public Restaurant updateRestaurantStatusByEvent(RestaurantChangeStatusEvent event) {
        log.info("Receiving change status event={}", event);
        return event.getStatus().getHandler()
            .apply(event)
            .orElseGet(() -> {
                log.info("Status not applied for event={}", event);
                return findById(event.getRestaurantId());
            });
    }

    @CachePut(cacheNames = "restaurants", key = "#restaurantStateDTO.getRestaurantId()")
    @Transactional
    public Restaurant updateConnectionSate(RestaurantStateDTO restaurantStateDTO) {
        Long restaurantId = restaurantStateDTO.getRestaurantId();
        ConnectionState state = restaurantStateDTO.getState();

        log.info("Updating connection state for restaurantId={} to state={}",restaurantId, state);

        Restaurant restaurant = findById(restaurantId);
        restaurant.changeConnectionState(state);
        return restaurantRepository.save(restaurant);
    }

}
