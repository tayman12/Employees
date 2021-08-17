package com.workmotion.employees.services;

import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;

public interface EmployeeService {

    Employee create(Employee employee);

    Employee sendEvent(String employeeId, EmployeeEvent event) throws Exception;

}
