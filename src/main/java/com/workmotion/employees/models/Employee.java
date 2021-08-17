package com.workmotion.employees.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    //TODO: mandatory parameters

    @Id
    public String id;

    public String firstName;
    public String lastName;

    public EmployeeState state;

    public String contractInfo;
    public int age;
}
