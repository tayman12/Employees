package com.workmotion.employees.controllers;

import com.workmotion.employees.dto.EmployeeEventDTO;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EntityNotFoundException;
import com.workmotion.employees.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public Employee create(@RequestBody @Validated Employee employee) {
        return employeeService.create(employee);
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable @Validated @NonNull String id) {
        try {
            return employeeService.getEmployee(id);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.message);
        }
    }

    @PostMapping("/{id}/state-event")
    public Employee sendEvent(@PathVariable @Validated @NonNull String id, @RequestBody @Validated EmployeeEventDTO eventDTO) {
        try {
            return employeeService.sendEvent(id, eventDTO.getEvent());
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.message);
        }
    }
}
