package br.com.ifood.ifoodconnection.model.dto;

import br.com.ifood.ifoodconnection.model.ConnectionState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@EqualsAndHashCode(of = "restaurantId")
@Getter
public class RestaurantStateDTO implements Serializable {

    private Long restaurantId;

    private ConnectionState state;

    @JsonCreator
    public RestaurantStateDTO(
            @JsonProperty("restaurantId") Long restaurantId,
            @JsonProperty("state") ConnectionState state) {
        this.restaurantId = restaurantId;
        this.state = state;
    }
}
