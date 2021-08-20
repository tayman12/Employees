package com.workmotion.employees.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    //TODO: add tests
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiException apiException = new ApiException();

        apiException.setMessage("Method argument not valid");
        apiException.setTimestamp(new Date());
        apiException.setStatus(status);
        apiException.setCode(ex.getClass().getSimpleName());

        apiException.setErrors(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> String.format("%s -> %s", x.getField(), x.getDefaultMessage()))
                .collect(Collectors.toList()));

        return new ResponseEntity<>(apiException.toMap(), headers, status);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(BaseException ex) {
        return new ResponseEntity<>(ex.toMap(), ex.getStatus());
    }
}
