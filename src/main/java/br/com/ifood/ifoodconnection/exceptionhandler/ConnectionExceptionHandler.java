package br.com.ifood.ifoodconnection.exceptionhandler;

import br.com.ifood.ifoodconnection.model.exception.ScheduleConflictDateTimeException;
import br.com.ifood.ifoodconnection.model.exception.ScheduleUnavailableStateException;
import br.com.ifood.ifoodconnection.service.exception.RestaurantNotFoundException;
import br.com.ifood.ifoodconnection.service.exception.ScheduleUnavailableNotFoundException;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ConnectionExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ScheduleConflictDateTimeException.class)
    public ResponseEntity<Object> handleScheduleDateTimeException(ScheduleConflictDateTimeException ex, WebRequest request) {
        Error error = new Error(HttpStatus.CONFLICT.value(), ex.getMessage());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({RestaurantNotFoundException.class, ScheduleUnavailableNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception ex, WebRequest request) {
        Error error = new Error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler({ScheduleUnavailableStateException.class})
    public ResponseEntity<Object> handleStateException(Exception ex, WebRequest request) {
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> messages = getMessages(ex.getBindingResult());
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), messages);
        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    private List<String> getMessages(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
            .map(e -> e.getField() + " " + e.getDefaultMessage())
            .collect(Collectors.toList());
    }

    @Getter
    private class Error {
        private Integer code;

        private List<String> messages;

        public Error(Integer code) {
            this.code = code;
            this.messages = new ArrayList<>();

        }

        public Error(Integer code, String messages) {
            this(code);
            this.addMessage(messages);
        }

        public Error(Integer code, List<String> messages) {
            this.code = code;
            this.messages = messages;
        }

        public void addMessage(String message) {
            this.messages.add(message);
        }
    }
}
