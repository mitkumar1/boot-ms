package com.nish.ms.controller;

import com.nish.ms.model.CustomerDTO;
import com.nish.ms.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;

/*
* Not working code-  getting error for clinet certificate
*
* */
@RestController
public class OpenAPIController {

    @Autowired
    private WebClient client;

    private String[] names = "abc, kevin, michel, mariya, linda, john, smith, pepa, peter, sam, tim".split(", ");
    Random randomNum = new Random();

    RestTemplate restTemplate =  new RestTemplate();

    @GetMapping(path = "/client/openapi")
    public ResponseEntity<User> getAllCustomersAsObject(){

        String name =  names[randomNum.nextInt(10)];

        return restTemplate.getForEntity("https://api.agify.io/?name=" + name, User.class);
        // Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
    }


}
