package com.workmotion.employees.services;

import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;

public interface EmployeeService {

    Employee create(Employee employee);

    Employee sendEventStateMachine(String employeeId, EmployeeEvent event) throws Exception;

    Employee sendEventKafka(String employeeId, EmployeeEvent event) throws Exception;

    Employee getEmployee(String employeeId) throws Exception;
}
