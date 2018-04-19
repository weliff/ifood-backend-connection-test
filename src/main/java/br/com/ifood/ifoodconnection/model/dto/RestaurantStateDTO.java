package br.com.ifood.ifoodconnection.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@EqualsAndHashCode
@Getter
public class RestaurantStateDTO implements Serializable {

    private Long restaurantId;

    private Boolean sendingKeepAliveSignal;

    @JsonCreator
    public RestaurantStateDTO(
            @JsonProperty("restaurantId") Long restaurantId,
            @JsonProperty("sendingKeepAliveSignal") Boolean sendingKeepAliveSignal) {
        this.restaurantId = restaurantId;
        this.sendingKeepAliveSignal = sendingKeepAliveSignal;
    }
}
