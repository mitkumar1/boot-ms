package com.kumar.mithlesh.controller;

import com.kumar.mithlesh.model.Customer;
import com.kumar.mithlesh.service.CustomerService;
import com.kumar.mithlesh.config.CustomerServiceConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerServiceConfig config;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Customer Service is UP and Running! 🚀");
    }

    // Get all customers
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getCustomers() {
        log.info("Fetching all customers");
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    // Get customer by ID
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable("id") @Min(value = 1, message = "Customer ID must be positive") Integer id) {
        log.info("Fetching customer with ID: {}", id);
        Customer customer = customerService.findById(id);
        return ResponseEntity.ok(customer);
    }

    // Create new customer
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        log.info("Creating new customer: {}", customer.getCustomerName());
        Customer savedCustomer = customerService.saveOrUpdate(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    // Update customer completely
    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable("id") @Min(value = 1, message = "Customer ID must be positive") Integer id,
            @Valid @RequestBody Customer customer) {
        log.info("Updating customer with ID: {}", id);
        customer.setCustomerId(id);
        Customer updatedCustomer = customerService.saveOrUpdate(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    // Partially update customer
    @PatchMapping("/customers/{id}")
    public ResponseEntity<Customer> partialUpdateCustomer(
            @PathVariable("id") @Min(value = 1, message = "Customer ID must be positive") Integer id,
            @RequestBody Customer payloadEntity) {
        log.info("Partially updating customer with ID: {}", id);
        
        Customer customerEntity = customerService.findById(id);
        boolean needUpdate = false;

        if (StringUtils.hasLength(payloadEntity.getCustomerName())) {
            customerEntity.setCustomerName(payloadEntity.getCustomerName());
            needUpdate = true;
        }

        if (StringUtils.hasLength(payloadEntity.getEmail())) {
            customerEntity.setEmail(payloadEntity.getEmail());
            needUpdate = true;
        }

        if (payloadEntity.getMobileNumber() != null && payloadEntity.getMobileNumber() > 0) {
            customerEntity.setMobileNumber(payloadEntity.getMobileNumber());
            needUpdate = true;
        }

        if (needUpdate) {
            Customer updatedCustomer = customerService.saveOrUpdate(customerEntity);
            return ResponseEntity.ok(updatedCustomer);
        }

        return ResponseEntity.ok(customerEntity);
    }

    // Delete customer
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable("id") @Min(value = 1, message = "Customer ID must be positive") Integer id) {
        log.info("Deleting customer with ID: {}", id);
        customerService.remove(id);
        return ResponseEntity.noContent().build();
    }
}

