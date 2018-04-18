package br.com.ifood.ifoodconnection.model.event;

import br.com.ifood.ifoodconnection.model.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RestaurantChangeStatusEvent {

    private Long restaurantId;

    private RestaurantStatus status;

    private LocalDateTime date;
}
