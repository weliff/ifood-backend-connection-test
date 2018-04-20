package br.com.ifood.ifoodconnection.service;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import br.com.ifood.ifoodconnection.model.dto.RestaurantStateDTO;
import br.com.ifood.ifoodconnection.model.event.RestaurantChangeStatusEvent;
import br.com.ifood.ifoodconnection.publisher.PublisherChangeRestaurantStatus;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import br.com.ifood.ifoodconnection.service.exception.RestaurantNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RestaurantService {

    private RestaurantRepository restaurantRepository;

    private PublisherChangeRestaurantStatus publisherChangeRestaurantStatus;

    public RestaurantService(RestaurantRepository restaurantRepository, PublisherChangeRestaurantStatus publisherChangeRestaurantStatus) {

        this.restaurantRepository = restaurantRepository;
        this.publisherChangeRestaurantStatus = publisherChangeRestaurantStatus;
    }

    public List<Restaurant> findAllByIds(List<Long> id) {
        return restaurantRepository.findAllById(id);
    }

    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(String.format("Not found the resource Restaurant with id=%s", id)));
    }

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

    @Transactional
    public Restaurant deleteScheduleUnavailable(Long restaurantId, Long scheduleId) {
        log.info("Removing schedule for restaurantId={}, scheduleId={}", restaurantId, scheduleId);
        Restaurant restaurant = findById(restaurantId);
        restaurant.removeScheduleUnavailable(scheduleId);
        return restaurantRepository.save(restaurant);
    }

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

    @Transactional
    public Restaurant updateConnectionSate(RestaurantStateDTO restaurantStateDTO) {
        Long restaurantId = restaurantStateDTO.getRestaurantId();
        Boolean connected = restaurantStateDTO.getSendingKeepAliveSignal();

        log.info("Updating connection for restaurantId={} to connected={}",restaurantId, connected);

        Restaurant restaurant = findById(restaurantId);
        restaurant.sendingKeepAliveSignal(connected);
        return restaurantRepository.save(restaurant);
    }

}
