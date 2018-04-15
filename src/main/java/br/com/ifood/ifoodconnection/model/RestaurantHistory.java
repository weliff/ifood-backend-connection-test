package br.com.ifood.ifoodconnection.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
public class RestaurantHistory implements PersistentAttributeInterceptable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private RestaurantStatus status;

    @Column(name = "connection_state")
    private ConnectionState connectionState;

    @JsonIgnore
    @Getter(AccessLevel.PRIVATE)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private LocalDateTime dateTime;

    /* FOR HIBERNATE */
    private RestaurantHistory() {
    }

    public RestaurantHistory(Restaurant restaurant) {
        this.status = restaurant.getStatus();
        this.connectionState = restaurant.getConnectionState();
        this.restaurant = restaurant;
        this.dateTime = LocalDateTime.now();
    }

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return null;
    }

    @Override
    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {

    }
}
