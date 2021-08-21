package com.workmotion.employees.dto;

import com.workmotion.employees.models.EmployeeEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEventDTO {

    //TODO: test constraints
    @NotNull
    EmployeeEvent event;
}
