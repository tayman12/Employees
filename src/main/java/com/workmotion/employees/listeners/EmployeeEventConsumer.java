package com.workmotion.employees.listeners;

import com.workmotion.employees.dto.KafkaEmployeeEvent;
import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.wrappers.EmployeeStateMachineWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeEventConsumer {

    private final EmployeeStateMachineWrapper employeeStateMachineWrapper;

    //TODO: handle errors
    @KafkaListener(topics = "${employees.topic}")
    public void receive(KafkaEmployeeEvent message) throws EntityNotFoundException {
        log.info("Employee event consumer received message {}", message);

        StateMachine<EmployeeState, EmployeeEvent> stateMachine = employeeStateMachineWrapper.build(message.getEmployeeId());

        employeeStateMachineWrapper.sendEvent(message.getEvent(), message.getEmployeeId(), stateMachine);

        log.debug("Employee event consumer processed message {} successfully", message);
    }
}
