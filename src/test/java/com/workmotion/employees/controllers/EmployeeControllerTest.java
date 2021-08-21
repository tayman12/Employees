package com.workmotion.employees.controllers;

import com.workmotion.employees.dto.EmployeeEventDTO;
import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
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

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeAll
    static void setupAll() {
        List<String> ports = new ArrayList<>();
        ports.add("9092:9093");
        kafkaContainer.setPortBindings(ports);

        kafkaContainer.start();
        mongoDBContainer.start();
    }

    @BeforeEach
    public void setup() {
        mongoTemplate.dropCollection(Employee.class);
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
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> employeeController.getEmployee("123"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals(EntityNotFoundException.class.getSimpleName(), ex.getCode());
        assertEquals("123", ex.getEntityId());
        assertEquals(Employee.class.getSimpleName(), ex.getEntityType());
        assertEquals("Employee with id [123] is not found", ex.getMessage());
        assertTrue(ex.getErrors().isEmpty());
    }

    @Test
    void getEmployeeReturnsHimCorrectly() throws EntityNotFoundException {
        Employee employee = Employee.builder().staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968").build();

        Employee savedEmployee = employeeController.create(employee);

        Employee foundEmployee = employeeController.getEmployee(savedEmployee.getId());

        validateEmployee(employee, foundEmployee);
    }

    @Test
    void sendEventReturnsNotFoundIfNoEmployeeExist() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                employeeController.sendEvent("123", new EmployeeEventDTO(EmployeeEvent.APPROVE)));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals(EntityNotFoundException.class.getSimpleName(), ex.getCode());
        assertEquals("123", ex.getEntityId());
        assertEquals(Employee.class.getSimpleName(), ex.getEntityType());
        assertEquals("Employee with id [123] is not found", ex.getMessage());
        assertTrue(ex.getErrors().isEmpty());
    }

    //TODO: search how to test kafka events
    @Test
    void sendEventSendsTheEventAndMakeTheStatusTransition() throws InterruptedException, EntityNotFoundException {
        Employee employee = Employee.builder().staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968").build();

        Employee savedEmployee = employeeController.create(employee);

        employeeController.sendEvent(savedEmployee.getId(), new EmployeeEventDTO(EmployeeEvent.CHECK));

        Thread.sleep(4000);

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
