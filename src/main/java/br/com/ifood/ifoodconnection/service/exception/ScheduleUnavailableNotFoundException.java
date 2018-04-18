package br.com.ifood.ifoodconnection.service.exception;

public class ScheduleUnavailableNotFoundException extends IllegalArgumentException {

    public ScheduleUnavailableNotFoundException(String message) {
        super(message);
    }

    public ScheduleUnavailableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
