package com.nish.ms.controller;

import com.nish.ms.model.Customer;
import com.nish.ms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(path = "/api/test")
    private String method() {
        return "test";
    }

    //creating a get mapping that retrieves all Customer details
    @GetMapping(path = "/api/v1/customers")
    private List<Customer> getCustomerList(){
        return this.customerService.findAll();
    }

    //creating a get mapping that retrieves a specific Customer detail based on id
    @GetMapping(path = "/api/v1/customers/{id}")
    private Customer getCustomerById(@PathVariable("id") int id){
        return customerService.findById(id);
    }

    //creating a delete mapping that deletes a specified Customer
    @DeleteMapping("/api/v1/customers/{id}")
    private Optional<Integer> deleteCustomerById(@PathVariable("id") int id) {
        return customerService.remove(id);
    }

    //creating post mapping that post the Customer detail in the database
    @PostMapping("/api/v1/customers")
    private  Customer saveCustomer(@RequestBody Customer entity) {
        return customerService.saveOrUpdate(entity);
    }

    //creating put mapping that updates the Customer detail
    @PutMapping("/api/v1/customers/{id}")
    private Customer updateCustomerById(@RequestBody Customer entity) {
        return customerService.saveOrUpdate(entity);
    }

    //    @PatchMapping("/api/v1/customers/{id}")
    @RequestMapping(method = RequestMethod.PATCH, value = "/api/v1/customers/{id}")
    public Customer updateCustomerEmailById(@RequestBody Customer payloadEntity, @PathVariable("id") int id) {
        Customer customerEntity =  customerService.findById(id);
        // patch logic is only applied over the message attribute,
        // so what ever we will pass from post man that is gets updated for Customer attribute
//        entity.setEmail(messageText);


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

