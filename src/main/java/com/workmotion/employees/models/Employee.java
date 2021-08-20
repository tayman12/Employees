package com.workmotion.employees.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    public String id;

    @NotBlank
    public String staffId;

    @NotBlank
    public String firstName;

    @NotBlank
    public String lastName;

    public EmployeeState state;

    @NotBlank
    public String mobileNo;

    @NotNull
    @Range(min = 1, max = 120)
    public Integer age;
}
