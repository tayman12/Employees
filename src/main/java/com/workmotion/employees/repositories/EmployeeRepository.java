package com.workmotion.employees.repositories;

import com.workmotion.employees.models.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
