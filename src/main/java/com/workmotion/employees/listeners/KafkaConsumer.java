package com.workmotion.employees.listeners;

import com.workmotion.employees.wrappers.EmployeeStateMachineWrapper;
import com.workmotion.employees.dto.KafkaEmployeeEvent;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final EmployeeStateMachineWrapper employeeStateMachineWrapper;

    @KafkaListener(topics = "${employees.topic}")
    public void receive(KafkaEmployeeEvent message) throws Exception {
        log.info("Kafka consumer received message " + message);

        StateMachine<EmployeeState, EmployeeEvent> stateMachine = employeeStateMachineWrapper.build(message.getEmployeeId());

        employeeStateMachineWrapper.sendEvent(message.getEvent(), message.getEmployeeId(), stateMachine);
    }
}
