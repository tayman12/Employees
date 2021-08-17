package com.workmotion.employees.services;

import com.workmotion.employees.listeners.EmployeeStateChangeInterceptor;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    //TODO: error handling and testing
    //TODO: error codes
    public static final String EMPLOYEE_ID_HEADER = "employee_id";

    private final EmployeeRepository employeeRepository;
    private final StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;
    private final EmployeeStateChangeInterceptor employeeStateChangeInterceptor;

    public Employee create(Employee employee) {
        employee.setState(EmployeeState.ADDED);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee sendEvent(String employeeId, EmployeeEvent event) throws Exception {
        StateMachine<EmployeeState, EmployeeEvent> stateMachine = build(employeeId);

        sendEvent(employeeId, stateMachine, event);

        return employeeRepository.findById(employeeId).get();
    }

    //TODO: check if there is a new/better way to do this
    private StateMachine<EmployeeState, EmployeeEvent> build(String employeeId) throws Exception {
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
            throw new Exception(String.format("No employees found with id [$s]", employeeId)); //TODO: convert this into an appropriate error code
        }
    }

    private void sendEvent(String employeeId, StateMachine<EmployeeState, EmployeeEvent> stateMachine, EmployeeEvent event) {
        Message message = MessageBuilder.withPayload(event)
                .setHeader(EMPLOYEE_ID_HEADER, employeeId)
                .build();

        stateMachine.sendEvent(message);
    }
}
