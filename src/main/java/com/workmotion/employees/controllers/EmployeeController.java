package com.workmotion.employees.controllers;

import com.workmotion.employees.dto.EmployeeEventDTO;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.services.EmployeeService;
import com.workmotion.employees.services.EmployeeServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        return employeeService.create(employee);
    }

    @PostMapping("/{id}/state-event")
    public Employee sendEvent(@PathVariable String id, @RequestBody EmployeeEventDTO eventDTO) throws Exception {
        return employeeService.sendEventStateMachine(id, eventDTO.getEvent());
    }

    @PostMapping("/{id}/state-event/kafka")
    public Employee sendEventKafka(@PathVariable String id, @RequestBody EmployeeEventDTO eventDTO) throws Exception {
        return employeeService.sendEventKafka(id, eventDTO.getEvent());
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable String id) throws Exception {
        return employeeService.getEmployee(id);
    }
}
