package com.example.demo.controller;

import com.example.demo.exception.*;
import com.example.demo.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    @Autowired
    private EmployeeRepository repository;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        logger.info("Get all the employees...");
        return repository.findAll();
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(value = "id") long employeeId) throws ResourceNotFoundException {
        logger.info("Get employee by id...");
        Employee employee = repository.findById(employeeId).
                orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id:: " + employeeId));
        return ResponseEntity.ok().body(employee);

    }

    @PostMapping("/employees")
    public Employee createEmployee(@Valid @RequestBody Employee employee) {
        logger.info("Insert employee...");
        return repository.save(employee);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> EmployeeById(@PathVariable(value = "id") long employeeId, @RequestBody Employee updatedEmployee) throws ResourceNotFoundException {
        logger.info("Update employee...");
        Employee employee = repository.findById(employeeId).
                orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id:: " + employeeId));
        employee.setName(updatedEmployee.getName());
        employee.setAge(updatedEmployee.getAge());
        repository.save(employee);
        return ResponseEntity.ok().body(employee);

    }

    @DeleteMapping("/employees/{id}")
    public void deleteEmployee(@PathVariable(value = "id") long employeeId) throws ResourceNotFoundException {
        logger.info("Delete employee...");
        Employee employee = repository.findById(employeeId).
                orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id:: " + employeeId));
        repository.delete(employee);
    }
}