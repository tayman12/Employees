package com.workmotion.employees.services;

import com.workmotion.employees.dto.KafkaEmployeeEvent;
import com.workmotion.employees.exceptions.EntityNotFoundException;
import com.workmotion.employees.models.Employee;
import com.workmotion.employees.models.EmployeeEvent;
import com.workmotion.employees.models.EmployeeState;
import com.workmotion.employees.repositories.EmployeeRepository;
import com.workmotion.employees.validator.EmployeeShouldExistByIdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    @Value(value = "${employees.topic}")
    public String employeesTopic;

    private final EmployeeRepository employeeRepository;
    private final KafkaTemplate<String, KafkaEmployeeEvent> kafkaTemplate;

    @Override
    public Employee create(Employee employee) {
        employee.setState(EmployeeState.ADDED);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployee(String employeeId) throws EntityNotFoundException {
        new EmployeeShouldExistByIdValidator(employeeId, employeeRepository).validate();

        return employeeRepository.findById(employeeId).get();
    }

    @Override
    public void sendEvent(String employeeId, EmployeeEvent event) throws EntityNotFoundException {
        new EmployeeShouldExistByIdValidator(employeeId, employeeRepository).validate();

        KafkaEmployeeEvent kafkaEmployeeEvent = new KafkaEmployeeEvent(event, employeeId);
        ListenableFuture<SendResult<String, KafkaEmployeeEvent>> future = kafkaTemplate.send(employeesTopic, kafkaEmployeeEvent);

        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, KafkaEmployeeEvent> result) {
                log.debug("Sent message=[" + kafkaEmployeeEvent + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.debug("Unable to send message=[" + kafkaEmployeeEvent + "] due to : " + ex.getMessage());
            }
        });
    }
}
