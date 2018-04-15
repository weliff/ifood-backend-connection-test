package br.com.ifood.ifoodconnection.model.exception;

public class ScheduleDateTimeException extends IllegalStateException {

    public ScheduleDateTimeException(String message) {
        super(message);
    }

    public ScheduleDateTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
