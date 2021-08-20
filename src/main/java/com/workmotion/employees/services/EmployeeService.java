package com.workmotion.employees.services;

import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EntityNotFoundException;

public interface EmployeeService {

    Employee create(Employee employee);

    Employee getEmployee(String employeeId) throws EntityNotFoundException;

    Employee sendEvent(String employeeId, EmployeeEvent event) throws EntityNotFoundException;
}
