package com.kumar.mithlesh.own.controller;

import com.kumar.mithlesh.own.model.Customer;
import com.kumar.mithlesh.own.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(path = "/api/test")
    private String method() {
        return "test";
    }

    // Example endpoint using standard ExecutorService (Java 17 compatible)
    @GetMapping(path = "/api/executor-service")
    public String executorServiceExample() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> result = executor.submit(() -> {
            Thread.sleep(1000);
            return "Processed in executor service: " + Thread.currentThread();
        });
        String response = result.get();
        executor.shutdown();
        return response;
    }

    @GetMapping(path = "/api/v1/customers")
    private List<Customer> getCustomerList(){
        return this.customerService.findAll();
    }

    @GetMapping(path = "/api/v1/customers/{id}")
    private Customer getCustomerById(@PathVariable("id") int id){
        return customerService.findById(id);
    }

    @DeleteMapping("/api/v1/customers/{id}")
    private Optional<Integer> deleteCustomerById(@PathVariable("id") int id) {
        return customerService.remove(id);
    }

    @PostMapping("/api/v1/customers")
    private  Customer saveCustomer(@RequestBody Customer entity) {
        return customerService.saveOrUpdate(entity);
    }

    @PutMapping("/api/v1/customers/{id}")
    private Customer updateCustomerById(@RequestBody Customer entity) {
        return customerService.saveOrUpdate(entity);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/api/v1/customers/{id}")
    public Customer updateCustomerEmailById(@RequestBody Customer payloadEntity, @PathVariable("id") int id) {
        Customer customerEntity =  customerService.findById(id);
        boolean needUpdate = false;
        if (StringUtils.hasLength(payloadEntity.getCustomerName())) {
            customerEntity.setCustomerName(payloadEntity.getCustomerName());
            needUpdate = true;
        }
        if (StringUtils.hasLength(payloadEntity.getEmail())) {
            customerEntity.setEmail(payloadEntity.getEmail());
            needUpdate = true;
        }
        if (payloadEntity.getMobileNumber() > 0) {
            customerEntity.setMobileNumber(payloadEntity.getMobileNumber());
            needUpdate = true;
        }
        if (needUpdate) {
            return customerService.saveOrUpdate(customerEntity);
        }
        return customerService.findById(id);
    }
}
