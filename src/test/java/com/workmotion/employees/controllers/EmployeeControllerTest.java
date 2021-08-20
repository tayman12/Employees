package com.workmotion.employees.controllers;

import com.workmotion.employees.dto.EmployeeEventDTO;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeControllerTest {

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo"));
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @BeforeAll
    static void setup() {
        mongoDBContainer.start();
        kafkaContainer.start();
    }

    @AfterAll
    static void tearDown() {
        mongoDBContainer.close();
        kafkaContainer.close();
    }

    @Test
    void creatingNewEmployeeAddsItWithAddedState() {
        Employee employee = Employee.builder().staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968").build();

        Employee savedEmployee = employeeController.create(employee);

        validateEmployee(employee, savedEmployee);

        List<Employee> allEmployees = employeeRepository.findAll();

        assertFalse(allEmployees.isEmpty());

        validateEmployee(employee, allEmployees.get(0));
    }

    @Test
    void getEmployeeReturnsNotFoundIfNoEmployeeExist() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> employeeController.getEmployee("123"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Employee with id [123] is not found", ex.getReason());
    }

    @Test
    void getEmployeeReturnsHimCorrectly() {
        Employee employee = Employee.builder().staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968").build();

        Employee savedEmployee = employeeController.create(employee);

        Employee foundEmployee = employeeController.getEmployee(savedEmployee.getId());

        validateEmployee(employee, foundEmployee);
    }

    @Test
    void sendEventReturnsNotFoundIfNoEmployeeExist() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> employeeController.sendEvent("123", new EmployeeEventDTO(EmployeeEvent.APPROVE)));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Employee with id [123] is not found", ex.getReason());
    }

    //TODO: search how to test kafka events
    @Test
    void sendEventSendsTheEventAndMakeTheStatusTransition() throws InterruptedException {
        Employee employee = Employee.builder().staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968").build();

        Employee savedEmployee = employeeController.create(employee);

        employeeController.sendEvent(savedEmployee.getId(), new EmployeeEventDTO(EmployeeEvent.CHECK));

        Thread.sleep(200);

        Employee updatedEmployee = employeeRepository.findById(savedEmployee.getId()).get();

        assertEquals(EmployeeState.IN_CHECK, updatedEmployee.getState());
    }

    private void validateEmployee(Employee expected, Employee actual) {
        assertEquals(expected.getStaffId(), actual.getStaffId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(expected.getMobileNo(), actual.getMobileNo());
        assertEquals(expected.getState(), actual.getState());
    }
}
