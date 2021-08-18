package com.workmotion.employees.listeners;

import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import com.workmotion.employees.services.EmployeeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmployeeStateChangeInterceptor extends StateMachineInterceptorAdapter<EmployeeState, EmployeeEvent> {

    @Value(value = "employees.header.id")
    public String employeesIdHeader;

    private final EmployeeRepository employeeRepository;

    @Override
    public void preStateChange(State<EmployeeState, EmployeeEvent> state, Message<EmployeeEvent> message,
                               Transition<EmployeeState, EmployeeEvent> transition, StateMachine<EmployeeState, EmployeeEvent> stateMachine,
                               StateMachine<EmployeeState, EmployeeEvent> rootStateMachine) {

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(msg.getHeaders().get(employeesIdHeader)).ifPresent(employeeId -> {
                employeeRepository.findById(employeeId.toString()).ifPresent(employee -> {
                    employee.setState(state.getId());
                    employeeRepository.save(employee);
                });
            });
        });

    }
}
