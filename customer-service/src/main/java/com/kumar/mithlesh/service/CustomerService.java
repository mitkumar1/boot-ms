package com.kumar.mithlesh.service;

import com.kumar.mithlesh.exception.ElementNotFoundException;
import com.kumar.mithlesh.model.Customer;
import com.kumar.mithlesh.repo.CustomerRepoIface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepoIface repo;

    public List<Customer> findAll() {
        log.info("Fetching all customers");
        List<Customer> customers = repo.findAll();
        log.info("Found {} customers", customers.size());
        return customers;
    }

    @Transactional
    public Customer add(Customer entity) {
        log.info("Adding new customer: {}", entity.getCustomerName());
        Customer savedCustomer = repo.save(entity);
        log.info("Customer saved with ID: {}", savedCustomer.getCustomerId());
        return savedCustomer;
    }

    public Customer findById(Integer id) {
        log.info("Finding customer by ID: {}", id);
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer with ID {} not found", id);
                    return new ElementNotFoundException("Customer with ID " + id + " not found");
                });
    }

    @Transactional
    public Optional<Integer> remove(Integer id) {
        log.info("Attempting to remove customer with ID: {}", id);
        
        if (repo.existsById(id)) {
            repo.deleteById(id);
            log.info("Customer with ID {} successfully deleted", id);
            return Optional.of(id);
        } else {
            log.warn("Customer with ID {} not found for deletion", id);
            throw new ElementNotFoundException("Customer with ID " + id + " not found");
        }
    }

    @Transactional
    public Customer saveOrUpdate(Customer entity) {
        if (entity.getCustomerId() != null) {
            log.info("Updating existing customer with ID: {}", entity.getCustomerId());
        } else {
            log.info("Creating new customer: {}", entity.getCustomerName());
        }
        
        Customer savedCustomer = repo.save(entity);
        log.info("Customer saved/updated with ID: {}", savedCustomer.getCustomerId());
        return savedCustomer;
    }

    // Additional business methods
    public boolean existsById(Integer id) {
        return repo.existsById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        log.info("Finding customer by email: {}", email);
        return repo.findByEmail(email);
    }

    public long countCustomers() {
        return repo.count();
    }
}

