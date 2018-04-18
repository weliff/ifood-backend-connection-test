package br.com.ifood.ifoodconnection.model.event;

import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class RestaurantChangeStatusEvent {

    private Long restaurantId;

    private RestaurantStatus status;

    private LocalDateTime date;

    @JsonCreator
    public RestaurantChangeStatusEvent(@JsonProperty("restaurantId") Long restaurantId,
                                       @JsonProperty("status") RestaurantStatus status,
                                       @JsonProperty("date") LocalDateTime date) {
        this.restaurantId = restaurantId;
        this.status = status;
        this.date = date;
    }
}
