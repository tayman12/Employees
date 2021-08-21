package com.workmotion.employees.controllers;

import com.workmotion.employees.dto.EmployeeEventDTO;
import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Employee create(@RequestBody @Validated Employee employee) {
        return employeeService.create(employee);
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable String id) throws EntityNotFoundException {
        return employeeService.getEmployee(id);
    }

    @PostMapping(value = "/{id}/state-event", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void sendEvent(@PathVariable String id, @RequestBody @Validated EmployeeEventDTO eventDTO) throws EntityNotFoundException {
        employeeService.sendEvent(id, eventDTO.getEvent());
    }
}
