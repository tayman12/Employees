package com.workmotion.employees.controllers;

import com.workmotion.employees.dto.EmployeeEventDTO;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.services.EmployeeService;
import com.workmotion.employees.services.EmployeeServiceImpl;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    //TODO: lift url to controller level
    @PostMapping("/api/v1/employees")
    public Employee create(@RequestBody Employee employee) {
        return employeeService.create(employee);
    }

    @PostMapping("/api/v1/employees/{id}/state-event")
    public Employee sendEvent(@PathVariable String id, @RequestBody EmployeeEventDTO eventDTO) throws Exception {
        return employeeService.sendEvent(id, eventDTO.getEvent());
    }
}
