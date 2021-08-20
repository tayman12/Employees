package com.workmotion.employees.validator;

import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeShouldExistByIdValidatorTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    public void validateThrowsIfNoEmployeeExists() {
        EmployeeShouldExistByIdValidator validator = new EmployeeShouldExistByIdValidator("123", employeeRepository);

        when(employeeRepository.findById("123")).thenReturn(Optional.empty());

        try {
            validator.validate();
        } catch (EntityNotFoundException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            assertEquals(EntityNotFoundException.class.getSimpleName(), ex.getCode());
            assertEquals("123", ex.getEntityId());
            assertEquals(Employee.class.getSimpleName(), ex.getEntityType());
            assertEquals("Employee with id [123] is not found", ex.getMessage());
            assertTrue(ex.getErrors().isEmpty());
        }
    }

    @Test
    public void validateDoesNothingIfEmployeeExists() throws EntityNotFoundException {
        EmployeeShouldExistByIdValidator validator = new EmployeeShouldExistByIdValidator("123", employeeRepository);

        Employee employee = Employee.builder().id("123").staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968").build();

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        validator.validate();

        assertTrue(true);
    }
}
