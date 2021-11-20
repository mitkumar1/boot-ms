package com.nish.ms.repo;

import com.nish.ms.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*
* Spring boot – CommandLineRunner interface
*
*  Spring boot’s CommandLineRunner interface is used to run a code block only once in application’s lifetime – after application is initialized.
*
*  Application Runner and Command Line Runner interfaces lets you to execute the code after the Spring Boot application is started.
*  You can use these interfaces to perform any actions immediately after the application has started.
*
*
* https://howtodoinjava.com/spring-boot/command-line-runner-interface-example/
* https://www.baeldung.com/spring-boot-console-app
*
* */
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
