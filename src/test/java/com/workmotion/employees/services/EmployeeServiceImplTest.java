package com.workmotion.employees.services;

import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceImplTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void creatingNewEmployeeAddsItWithAddedState() {
        Employee employee = Employee.builder().firstName("Tocka").lastName("Ayman").build();

        Employee savedEmployee = employeeService.create(employee);

        assertEquals(EmployeeState.ADDED, savedEmployee.getState());
    }

    @Test
    void sendingStateEventUpdatesEmployeeStateCorrectly() throws Exception {
        Employee employee = Employee.builder().firstName("Tocka").lastName("Ayman").build();

        Employee savedEmployee = employeeService.create(employee);

        employeeService.sendEventStateMachine(savedEmployee.getId(), EmployeeEvent.CHECK);
        assertEquals(EmployeeState.IN_CHECK, employeeRepository.findById(savedEmployee.getId()).get().getState());

        employeeService.sendEventStateMachine(savedEmployee.getId(), EmployeeEvent.APPROVE);
        assertEquals(EmployeeState.APPROVED, employeeRepository.findById(savedEmployee.getId()).get().getState());

        employeeService.sendEventStateMachine(savedEmployee.getId(), EmployeeEvent.ACTIVATE);
        assertEquals(EmployeeState.ACTIVE, employeeRepository.findById(savedEmployee.getId()).get().getState());
    }
}
