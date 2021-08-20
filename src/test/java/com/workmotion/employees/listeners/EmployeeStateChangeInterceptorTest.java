package com.workmotion.employees.listeners;

import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeStateChangeInterceptorTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private State state;

    @Mock
    private Message message;

    @Mock
    private MessageHeaders messageHeaders;

    @Mock
    private Transition transition;

    @Mock
    private StateMachine<EmployeeState, EmployeeEvent> stateMachine;

    @Captor
    ArgumentCaptor<Employee> employeeArgumentCaptor;

    private EmployeeStateChangeInterceptor employeeStateChangeInterceptor;

    @Test
    public void receivingNullMessageIgnoresMessage() {
        employeeStateChangeInterceptor = new EmployeeStateChangeInterceptor(employeeRepository);

        employeeStateChangeInterceptor.preStateChange(state, null, transition, stateMachine, stateMachine);

        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    public void receivingNullMessageHeaderIgnoresMessage() {
        employeeStateChangeInterceptor = new EmployeeStateChangeInterceptor(employeeRepository);

        when(message.getHeaders()).thenReturn(messageHeaders);
        when(messageHeaders.get(null)).thenReturn(null);

        employeeStateChangeInterceptor.preStateChange(state, message, transition, stateMachine, stateMachine);

        verify(employeeRepository, times(0)).save(any());
    }

    @Test
    public void receivingNonExistingEmployeeIdIgnoresMessage() {
        employeeStateChangeInterceptor = new EmployeeStateChangeInterceptor(employeeRepository);

        when(message.getHeaders()).thenReturn(messageHeaders);
        when(messageHeaders.get(null)).thenReturn("123");
        when(employeeRepository.findById("123")).thenReturn(Optional.empty());

        employeeStateChangeInterceptor.preStateChange(state, message, transition, stateMachine, stateMachine);

        verify(employeeRepository, times(0)).save(any());
    }

    //TODO: set values instead of null
    @Test
    public void receivingMessageSetsStateAndSavesEmployee() {
        employeeStateChangeInterceptor = new EmployeeStateChangeInterceptor(employeeRepository);

        Employee employee = Employee.builder().id("123").staffId("456").firstName("Tocka").lastName("Ayman").age(9).mobileNo("01128821968")
                .state(EmployeeState.ADDED).build();

        when(state.getId()).thenReturn(EmployeeState.ACTIVE);
        when(message.getHeaders()).thenReturn(messageHeaders);
        when(messageHeaders.get(null)).thenReturn("123");
        when(employeeRepository.findById("123")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employeeArgumentCaptor.capture())).thenReturn(employee);

        employeeStateChangeInterceptor.preStateChange(state, message, transition, stateMachine, stateMachine);

        verify(employeeRepository, times(1)).save(any());

        assertEquals(EmployeeState.ACTIVE, employeeArgumentCaptor.getValue().getState());
    }
}
