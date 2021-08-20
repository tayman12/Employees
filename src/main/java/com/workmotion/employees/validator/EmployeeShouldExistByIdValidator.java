package com.workmotion.employees.validator;

import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EntityNotFoundException;
import com.workmotion.employees.repositories.EmployeeRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmployeeShouldExistByIdValidator implements BaseValidator {

    private String employeeId;
    private EmployeeRepository employeeRepository;

    @Override
    public void validate() throws EntityNotFoundException {
        employeeRepository.findById(employeeId).orElseThrow(() ->
                new EntityNotFoundException(employeeId, Employee.class.getSimpleName()));
    }
}
