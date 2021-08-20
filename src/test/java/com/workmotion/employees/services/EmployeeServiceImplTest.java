package com.workmotion.employees.services;

import com.workmotion.employees.dto.KafkaEmployeeEvent;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private KafkaTemplate<String, KafkaEmployeeEvent> kafkaTemplate;

    private EmployeeService employeeService;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    ArgumentCaptor<KafkaEmployeeEvent> kafkaEmployeeEventArgumentCaptor;

    @Test
    void creatingNewEmployeeAddsItWithAddedState() {
        employeeService = new EmployeeServiceImpl(employeeRepository, kafkaTemplate);

        Employee employee = Employee.builder().staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968").build();

        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee savedEmployee = employeeService.create(employee);

        assertEquals("456", savedEmployee.getStaffId());
        assertEquals("Tocka", savedEmployee.getFirstName());
        assertEquals("Ayman", savedEmployee.getLastName());
        assertEquals(9, savedEmployee.getAge());
        assertEquals("01128821968", savedEmployee.getMobileNo());
        assertEquals(EmployeeState.ADDED, savedEmployee.getState());

        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void getEmployeeThrowsEntityNotFoundExceptionIfEmployeeNotFound() {
        employeeService = new EmployeeServiceImpl(employeeRepository, kafkaTemplate);

        when(employeeRepository.findById("123")).thenReturn(Optional.empty());

        try {
            employeeService.getEmployee("123");

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
    void getEmployeeCallsRepoToGetEmployee() throws EntityNotFoundException {
        employeeService = new EmployeeServiceImpl(employeeRepository, kafkaTemplate);

        Employee employee = Employee.builder().id("123").staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968")
                .state(EmployeeState.ADDED).build();

        when(employeeRepository.findById("123")).thenReturn(Optional.of(employee));

        Employee resultedEmployee = employeeService.getEmployee(employee.getId());

        assertEquals("456", resultedEmployee.getStaffId());
        assertEquals("Tocka", resultedEmployee.getFirstName());
        assertEquals("Ayman", resultedEmployee.getLastName());
        assertEquals(9, resultedEmployee.getAge());
        assertEquals("01128821968", resultedEmployee.getMobileNo());
        assertEquals(EmployeeState.ADDED, resultedEmployee.getState());

        verify(employeeRepository, times(2)).findById(employee.getId());
    }

    @Test
    void sendingEventThrowsEntityNotFoundExceptionIfEmployeeNotFound() {
        employeeService = new EmployeeServiceImpl(employeeRepository, kafkaTemplate);

        when(employeeRepository.findById("123")).thenReturn(Optional.empty());

        try {
            employeeService.sendEvent("123", EmployeeEvent.ACTIVATE);

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
    void sendingEventSendsKafkaEvent() throws EntityNotFoundException {
        employeeService = new EmployeeServiceImpl(employeeRepository, kafkaTemplate);

        Employee employee = Employee.builder().id("123").staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968")
                .state(EmployeeState.ADDED).build();

        ListenableFuture<SendResult<String, KafkaEmployeeEvent>> future = mock(ListenableFuture.class);

        when(employeeRepository.findById("123")).thenReturn(Optional.of(employee));
        when(kafkaTemplate.send(stringArgumentCaptor.capture(), kafkaEmployeeEventArgumentCaptor.capture())).thenReturn(future);

        employeeService.sendEvent("123", EmployeeEvent.ACTIVATE);

        assertNull(stringArgumentCaptor.getValue()); //TODO: inject topic name?
        assertEquals(employee.getId(), kafkaEmployeeEventArgumentCaptor.getValue().getEmployeeId());
        assertEquals(EmployeeEvent.ACTIVATE, kafkaEmployeeEventArgumentCaptor.getValue().getEvent());

        verify(kafkaTemplate, times(1)).send(null, new KafkaEmployeeEvent(EmployeeEvent.ACTIVATE, employee.getId()));
    }
}
