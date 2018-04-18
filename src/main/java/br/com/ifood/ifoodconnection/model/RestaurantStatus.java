package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.config.ContextBeanHolder;
import br.com.ifood.ifoodconnection.publisher.PublisherChangeRestaurantStatus;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import br.com.ifood.ifoodconnection.service.stauts.StatusChangeAvailableHandler;
import br.com.ifood.ifoodconnection.service.stauts.StatusChangeEventHandler;
import br.com.ifood.ifoodconnection.service.stauts.StatusChangeUnavailableHandler;

public enum RestaurantStatus {

    AVAILABLE {
        @Override
        public StatusChangeEventHandler getHandler() {
            RestaurantRepository restaurantRepository = ContextBeanHolder.getBean(RestaurantRepository.class);
            return new StatusChangeAvailableHandler(restaurantRepository);
        }
    },
    UNAVAILABLE {
        @Override
        public StatusChangeEventHandler getHandler() {
            RestaurantRepository restaurantRepository = ContextBeanHolder.getBean(RestaurantRepository.class);
            PublisherChangeRestaurantStatus publisher = ContextBeanHolder.getBean(PublisherChangeRestaurantStatus.class);
            return new StatusChangeUnavailableHandler(restaurantRepository, publisher);
        }
    };

    public abstract StatusChangeEventHandler getHandler();

}
