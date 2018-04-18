package br.com.ifood.ifoodconnection.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Entity
public class RestaurantHistory implements PersistentAttributeInterceptable, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private RestaurantStatus status;

    @Column(name = "connection_state")
    private ConnectionState connectionState;

    private LocalDateTime dateTime;

    @JsonIgnore
    @Getter(AccessLevel.PRIVATE)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnore
    @Transient
    private PersistentAttributeInterceptor persistentAttributeInterceptor;

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
        return persistentAttributeInterceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.persistentAttributeInterceptor = persistentAttributeInterceptor;
    }
}
