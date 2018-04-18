package br.com.ifood.ifoodconnection.model.exception;

public class ScheduleConflictDateTimeException extends IllegalStateException {

    public ScheduleConflictDateTimeException(String message) {
        super(message);
    }

    public ScheduleConflictDateTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
