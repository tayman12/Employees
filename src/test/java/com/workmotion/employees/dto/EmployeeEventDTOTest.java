package com.workmotion.employees.dto;

import com.workmotion.employees.models.EmployeeEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeEventDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void nullEventThrows() {
        EmployeeEventDTO employeeEventDTO = new EmployeeEventDTO();

        Set<ConstraintViolation<EmployeeEventDTO>> violations = validator.validate(employeeEventDTO);

        assertEquals(1, violations.size());

        assertEquals("event", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotNull", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void validEmployeeDoesNotThrow() {
        EmployeeEventDTO employeeEventDTO = new EmployeeEventDTO(EmployeeEvent.APPROVE);

        Set<ConstraintViolation<EmployeeEventDTO>> violations = validator.validate(employeeEventDTO);

        assertTrue(violations.isEmpty());
    }
}
