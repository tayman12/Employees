package com.workmotion.employees.builders;

import com.workmotion.employees.listeners.EmployeeStateChangeInterceptor;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class EmployeeStateMachineBuilder {

    private final EmployeeRepository employeeRepository;
    private final StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;
    private final EmployeeStateChangeInterceptor employeeStateChangeInterceptor;

    //TODO: check if there is a new/better way to do this
    public StateMachine<EmployeeState, EmployeeEvent> build(String employeeId) throws Exception {
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
}
