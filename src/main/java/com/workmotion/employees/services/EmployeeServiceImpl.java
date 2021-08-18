package com.workmotion.employees.services;

import com.workmotion.employees.dto.KafkaEmployeeEvent;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import com.workmotion.employees.wrappers.EmployeeStateMachineWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    //TODO: error handling and testing
    //TODO: error codes
    @Value(value = "${employees.topic}")
    public String employeesTopic;

    private final EmployeeRepository employeeRepository;
    private final EmployeeStateMachineWrapper employeeStateMachineWrapper;
    private final KafkaTemplate<String, KafkaEmployeeEvent> kafkaTemplate;

    public Employee create(Employee employee) {
        employee.setState(EmployeeState.ADDED);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee sendEventStateMachine(String employeeId, EmployeeEvent event) throws Exception {
        StateMachine<EmployeeState, EmployeeEvent> stateMachine = employeeStateMachineWrapper.build(employeeId);

        employeeStateMachineWrapper.sendEvent(event, employeeId, stateMachine);

        return employeeRepository.findById(employeeId).get();
    }

    //TODO: what should be returned here
    @Override
    public Employee sendEventKafka(String employeeId, EmployeeEvent event) throws ExecutionException, InterruptedException {
        KafkaEmployeeEvent kafkaEmployeeEvent = new KafkaEmployeeEvent(event, employeeId);
        ListenableFuture<SendResult<String, KafkaEmployeeEvent>> future = kafkaTemplate.send(employeesTopic, kafkaEmployeeEvent);

        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, KafkaEmployeeEvent> result) {
                System.out.println("Sent message=[" + kafkaEmployeeEvent + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[" + kafkaEmployeeEvent + "] due to : " + ex.getMessage());
            }
        });

        return employeeRepository.findById(employeeId).get();
    }
}
