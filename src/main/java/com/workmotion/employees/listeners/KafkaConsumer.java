package com.workmotion.employees.listeners;

import com.workmotion.employees.dto.KafkaEmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${employees.topic}")
    public void receive(KafkaEmployeeEvent message) {
        log.info(message.toString());



//        Optional.ofNullable(message).ifPresent(msg -> {
//            Optional.ofNullable(msg.getHeaders().get(employeesIdHeader)).ifPresent(employeeId -> {
//                employeeRepository.findById(employeeId.toString()).ifPresent(employee -> {
//                    employee.setState(state.getId());
//                    employeeRepository.save(employee);
//                });
//            });
//        });

    }
}
