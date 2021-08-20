package com.workmotion.employees.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.workmotion.employees.dto.EmployeeEventDTO;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import io.swagger.util.Json;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo"));
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @BeforeAll
    static void setupAll() {
        List<String> ports = new ArrayList<>();
        ports.add("9092:9093");
        kafkaContainer.setPortBindings(ports);

        kafkaContainer.start();
        mongoDBContainer.start();

        Json.mapper().registerModule(new JavaTimeModule());
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
    public void creatingEmployeeSavesHimIntoDB() throws Exception {
        Map<String, Object> employee = new HashMap<>();

        employee.put("staffId", "123");
        employee.put("firstName", "Tocka");
        employee.put("lastName", "Ayman");
        employee.put("mobileNo", "01128821968");
        employee.put("age", 4);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid("employees.yaml"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Employee responseEmployee = objectMapper.readValue(content, Employee.class);

        Employee expected = new Employee(null, "123", "Tocka", "Ayman", EmployeeState.ADDED, "01128821968", 4);

        validateEmployee(expected, responseEmployee);

        List<Employee> employees = employeeRepository.findAll();

        assertEquals(1, employees.size());
        validateEmployee(expected, employees.get(0));
    }

    @Test
    public void gettingEmployeeReturnsHimFromDB() throws Exception {
        Employee employee = new Employee();

        employee.setId("789");
        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setMobileNo("01128821968");
        employee.setAge(4);
        employee.setState(EmployeeState.ADDED);

        employeeRepository.save(employee);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/789")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid("employees.yaml"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Employee responseEmployee = objectMapper.readValue(content, Employee.class);

        validateEmployee(employee, responseEmployee);
    }

    @Test
    public void sendingEmployeeEventUpdatesEmployeeState() throws Exception {
        Employee employee = new Employee();

        employee.setId("789");
        employee.setStaffId("123");
        employee.setFirstName("Tocka");
        employee.setLastName("Ayman");
        employee.setMobileNo("01128821968");
        employee.setAge(4);
        employee.setState(EmployeeState.ADDED);

        employeeRepository.save(employee);

        EmployeeEventDTO employeeEventDTO = new EmployeeEventDTO(EmployeeEvent.CHECK);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/789/state-event")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(employeeEventDTO)))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid("employees.yaml"))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Employee responseEmployee = objectMapper.readValue(content, Employee.class);

        validateEmployee(employee, responseEmployee);

        Thread.sleep(2000);

        employee.setState(EmployeeState.IN_CHECK);

        List<Employee> employees = employeeRepository.findAll();

        assertEquals(1, employees.size());
        validateEmployee(employee, employees.get(0));
    }

    private void validateEmployee(Employee expected, Employee actual) {
        if (expected.getId() == null || expected.getId().isBlank()) {
            assertNotNull(actual.getId());
            assertFalse(actual.getId().isBlank());
        } else {
            assertEquals(expected.getId(), actual.getId());
        }

        assertEquals(expected.getStaffId(), actual.getStaffId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(expected.getMobileNo(), actual.getMobileNo());
        assertEquals(expected.getState(), actual.getState());
    }
}
