package com.workmotion.employees.dto;

import com.workmotion.employees.models.EmployeeEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaEmployeeEvent {

    @NotNull
    public EmployeeEvent event;

    @NotBlank
    public String employeeId;
}
