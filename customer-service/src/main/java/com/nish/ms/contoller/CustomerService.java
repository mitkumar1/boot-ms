package com.nish.ms.contoller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerService {

    @GetMapping(path="/api/hello")
    public String method(){
        return "hello";
    }
}
