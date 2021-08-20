package com.workmotion.employees.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class EntityNotFoundException extends BaseException {

    private String entityId;
    private String entityType;

    public EntityNotFoundException(String entityId, String entityType) {
        setMessage(String.format("%s not found", entityType));
        setStatus(HttpStatus.NOT_FOUND);
        getErrors().add(String.format("%s with id [%s] is not found", entityType, entityId));

        this.entityId = entityId;
        this.entityType = entityType;
    }
}
