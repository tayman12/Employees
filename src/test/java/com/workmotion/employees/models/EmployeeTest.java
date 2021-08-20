package com.workmotion.employees.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void nullStaffIdThrows() {
        Employee employee = new Employee();

        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);
        employee.setMobileNo("01128821968");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("staffId", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void blankStaffIdThrows() {
        Employee employee = new Employee();

        employee.setStaffId("");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);
        employee.setMobileNo("01128821968");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("staffId", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void nullFirstNameThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);
        employee.setMobileNo("01128821968");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("firstName", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void blankFirstNameThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);
        employee.setMobileNo("01128821968");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("firstName", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void nullLastNameThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);
        employee.setMobileNo("01128821968");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("lastName", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void blankLastNameThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);
        employee.setMobileNo("01128821968");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("lastName", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void nullMobileNoThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("mobileNo", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void blankMobileNoThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setAge(8);
        employee.setMobileNo("");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("mobileNo", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotBlank", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void nullAgeThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setMobileNo("01128821968");

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("age", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface javax.validation.constraints.NotNull", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void zeroAgeThrows() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setMobileNo("01128821968");
        employee.setAge(0);

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertEquals(1, violations.size());

        assertEquals("age", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
        assertEquals("interface org.hibernate.validator.constraints.Range", ((ConstraintViolation) violations.toArray()[0]).getConstraintDescriptor().getAnnotation().annotationType().toString());
    }

    @Test
    public void validEmployeeDoesNotThrow() {
        Employee employee = new Employee();

        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setState(EmployeeState.ADDED);
        employee.setMobileNo("01128821968");
        employee.setAge(4);

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        assertTrue(violations.isEmpty());
    }
}
