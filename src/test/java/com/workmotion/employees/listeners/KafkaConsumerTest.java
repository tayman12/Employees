package com.workmotion.employees.listeners;

import com.workmotion.employees.dto.KafkaEmployeeEvent;
import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.wrappers.EmployeeStateMachineWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private EmployeeStateMachineWrapper employeeStateMachineWrapper;

    @Mock
    private StateMachine<EmployeeState, EmployeeEvent> stateMachine;

    private EmployeeEventConsumer employeeEventConsumer;

    @Test
    public void receivingKafkaEmployeeCascadesEntityNotFoundException() throws EntityNotFoundException {
        employeeEventConsumer = new EmployeeEventConsumer(employeeStateMachineWrapper);

        when(employeeStateMachineWrapper.build("123")).thenThrow(new EntityNotFoundException("123", Employee.class.getSimpleName()));

        try {
            employeeEventConsumer.receive(new KafkaEmployeeEvent(EmployeeEvent.CHECK, "123"));

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
    public void receivingKafkaEmployeeEventCreatesStateMachineEvent() throws EntityNotFoundException {
        employeeEventConsumer = new EmployeeEventConsumer(employeeStateMachineWrapper);

        when(employeeStateMachineWrapper.build("123")).thenReturn(stateMachine);
        doNothing().when(employeeStateMachineWrapper).sendEvent(EmployeeEvent.CHECK, "123", stateMachine);

        employeeEventConsumer.receive(new KafkaEmployeeEvent(EmployeeEvent.CHECK, "123"));

        verify(employeeStateMachineWrapper, times(1)).sendEvent(EmployeeEvent.CHECK, "123", stateMachine);
    }
}