package com.workmotion.employees.wrappers;

import com.workmotion.employees.listeners.EmployeeStateChangeInterceptor;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmployeeStateMachineWrapper {

    @Value(value = "employees.header.id")
    public String employeesIdHeader;

    private final EmployeeRepository employeeRepository;
    private final StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;
    private final EmployeeStateChangeInterceptor employeeStateChangeInterceptor;

    //TODO: check if there is a new/better way to do this
    public StateMachine<EmployeeState, EmployeeEvent> build(String employeeId) throws EntityNotFoundException {
        Optional<Employee> employee = employeeRepository.findById(employeeId);

        if (employee.isPresent()) {
            StateMachine<EmployeeState, EmployeeEvent> stateMachine = stateMachineFactory.getStateMachine(employeeId);

            stateMachine.stop();

            stateMachine.getStateMachineAccessor()
                    .doWithAllRegions(stateMachineAccessor -> {
                        stateMachineAccessor.addStateMachineInterceptor(employeeStateChangeInterceptor);
                        stateMachineAccessor.resetStateMachine(new DefaultStateMachineContext<>(employee.get().getState(), null, null, null));
                    });

            stateMachine.start();

            return stateMachine;

        } else {
            throw new EntityNotFoundException(employeeId, Employee.class.getSimpleName());
        }
    }

    public void sendEvent(EmployeeEvent event, String employeeId, StateMachine<EmployeeState, EmployeeEvent> stateMachine) {
        Message message = MessageBuilder.withPayload(event)
                .setHeader(employeesIdHeader, employeeId)
                .build();

        stateMachine.sendEvent(message);
    }
}
