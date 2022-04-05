package com.nish.ms.controller;


import com.nish.ms.model.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class CustomerClientController {

    @Autowired
    private WebClient client;

    // working well with both type of uri
    private final String customerServiceURL = "lb://CUSTOMER-SERVICE/api/v1/customers";
//    private final String customerServiceURL = "//CUSTOMER-SERVICE/api/v1/customers";


    @GetMapping(path = "/client/srch/customers")
    public Mono<CustomerDTO[]> getAllCustomersAsObject(){

        return client
                .get()
                .uri(customerServiceURL)
                .retrieve()
                .bodyToMono(CustomerDTO[].class);

    }

    @GetMapping(path = "/client/srch/customers/{id}")
    public Mono<CustomerDTO> getCustomerByIdAsObject(@PathVariable("id") int id){

        return client
                .get()
                .uri(customerServiceURL + "/" + id)
                .retrieve()
                .bodyToMono(CustomerDTO.class);

    }


    @GetMapping(path = "/client/customers")
    public Mono<String> getAllCustomers(){

        return client
                .get()
                .uri(customerServiceURL)
                .retrieve()
                .bodyToMono(String.class);

    }

    @GetMapping(path = "/client/customers/{id}")
    public Mono<String> getCustomerById(@PathVariable("id") int id){

        return client
                .get()
                .uri(customerServiceURL + "/{id}", id)
                .retrieve()
                .bodyToMono(String.class);

    }

    @PostMapping(path = "/client/customers")
    public Mono<CustomerDTO> createCustomer(@RequestBody CustomerDTO dto){

        return client.post()
                .uri(customerServiceURL)
                .body(Mono.just(dto), CustomerDTO.class)
                .retrieve()
                .bodyToMono(CustomerDTO.class);

    }

    @DeleteMapping(path = "/client/customers/{id}")
    public Mono<Void> removeCustomerById(@PathVariable("id") int id){

        return client.delete()
                .uri(customerServiceURL + "/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

}

