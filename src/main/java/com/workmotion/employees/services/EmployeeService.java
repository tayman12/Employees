package com.workmotion.employees.services;

import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;

public interface EmployeeService {

    Employee create(Employee employee);

    Employee getEmployee(String employeeId) throws EntityNotFoundException;

    void sendEvent(String employeeId, EmployeeEvent event) throws EntityNotFoundException;
}
