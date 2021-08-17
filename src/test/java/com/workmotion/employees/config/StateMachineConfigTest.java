package com.workmotion.employees.config;

import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<EmployeeState, EmployeeEvent> factory;

    @Test
    void stateMachineTransitionsFromStateToAnotherCorrectly() {
        StateMachine<EmployeeState, EmployeeEvent> stateMachine = factory.getStateMachine();

        stateMachine.start();

        assertEquals(EmployeeState.ADDED, stateMachine.getState().getId());

        stateMachine.sendEvent(EmployeeEvent.CHECK);
        assertEquals(EmployeeState.IN_CHECK, stateMachine.getState().getId());

        stateMachine.sendEvent(EmployeeEvent.APPROVE);
        assertEquals(EmployeeState.APPROVED, stateMachine.getState().getId());

        stateMachine.sendEvent(EmployeeEvent.ACTIVATE);
        assertEquals(EmployeeState.ACTIVE, stateMachine.getState().getId());
    }
}
