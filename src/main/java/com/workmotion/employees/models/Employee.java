package com.workmotion.employees.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.lang.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    //TODO: mandatory parameters

    @Id
    public String id;

    @NonNull
    @Indexed(unique = true) // do we need validator here?
    public String staffId;

    @NonNull
    public String firstName;

    @NonNull
    public String lastName;

    public EmployeeState state;

    @NonNull
    public String mobileNo;

    @NonNull
    public int age;
}
