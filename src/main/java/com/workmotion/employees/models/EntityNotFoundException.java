package com.workmotion.employees.models;

import lombok.Data;

@Data
public class EntityNotFoundException extends BaseException {

    private String entityId;
    private String entityType;

    public EntityNotFoundException(String entityId, String entityType) {
        super(String.format("%s with id [%s] is not found", entityType, entityId));

        this.entityId = entityId;
        this.entityType = entityType;
    }
}
