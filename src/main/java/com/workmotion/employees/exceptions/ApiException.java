package com.workmotion.employees.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiException extends Exception {

    public String code;

    public String message;

    public HttpStatus status;

    public List<String> errors = new ArrayList<>();

    public Date timestamp = new Date();

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("code", code);
        map.put("message", message);
        map.put("timestamp", timestamp);
        map.put("status", status.value());
        map.put("errors", errors);

        return map;
    }
}
