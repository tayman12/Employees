package com.workmotion.employees.services;

import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.exceptions.EntityNotFoundException;

public interface EmployeeService {

    Employee create(Employee employee);

    Employee getEmployee(String employeeId) throws EntityNotFoundException;

    Employee sendEvent(String employeeId, EmployeeEvent event) throws EntityNotFoundException;
}
