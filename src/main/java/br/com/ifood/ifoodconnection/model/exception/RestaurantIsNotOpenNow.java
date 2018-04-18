package br.com.ifood.ifoodconnection.model.exception;

public class RestaurantIsNotOpenNow extends RuntimeException {

    public RestaurantIsNotOpenNow(String message) {
        super(message);
    }
}
