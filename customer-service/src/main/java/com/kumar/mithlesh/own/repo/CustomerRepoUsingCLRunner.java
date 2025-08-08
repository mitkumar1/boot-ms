package com.kumar.mithlesh.own.repo;

import com.kumar.mithlesh.own.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CustomerRepoUsingCLRunner implements CommandLineRunner {
    @Autowired
    private CustomerRepoIface customerRepo;
    @Override
    public void run(String... args) throws Exception {
        customerRepo.save(new Customer(201,"Rachel", "rachel@abc.com", 81111111));
        customerRepo.save(new Customer(202,"Tressa", "tressa@abc.com", 82222222));
        customerRepo.save(new Customer(203,"Kevin", "kevin@abc.com", 83333333));
        customerRepo.save(new Customer(204,"Smith", "smith@abc.com", 84444444));
        customerRepo.save(new Customer(205,"Tony", "tony@abc.com", 84555555));
    }
}
