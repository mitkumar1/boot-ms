package com.nish.ms;

import com.nish.ms.model.Customer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/*
* https://www.baeldung.com/rest-template
* https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/
* */
public class RestTemplateDemo {

    private final String URI_CUSTOMERS = "http://localhost:4040/api/v1/customers";
    private final String URI_CUSTOMERS_ID = "http://localhost:4040/api/v1/customers/{id}";


    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.nish.ms.model");
        context.refresh();

        RestTemplate restTemplate = context.getBean("restTemplate", RestTemplate.class);

        RestTemplateDemo ref =  new RestTemplateDemo();
        ref.restTemplateGETmthd(restTemplate);
        System.out.println("-------------------------------");
        ref.restTemplateGETmthd1(restTemplate);

    }

    /*
     * getForEntity() - Get Plain JSON
     *
     * */
    private void restTemplateGETmthd(RestTemplate restTemplate) {

        // "/api/v1/customers"
        Customer[] customerArray = restTemplate.getForObject(URI_CUSTOMERS, Customer[].class);

        Arrays.stream(customerArray).forEach(System.out::println);

        // "/api/v1/customers/{id}"
        Map<String, String> params = new HashMap<>();
        params.put("id", "205");
        Customer customer = restTemplate.getForObject(URI_CUSTOMERS_ID, Customer.class, params);

        System.out.println("customer = " + customer);

    }

    /*
    * getForObject() - Retrieving POJO Instead of JSON
    *
    * */
    private void restTemplateGETmthd1(RestTemplate restTemplate) {

        // "/api/v1/customers"
        ResponseEntity<Customer[]> responseArray = restTemplate.getForEntity(URI_CUSTOMERS, Customer[].class);

        if(responseArray.getStatusCode().equals(HttpStatus.OK))
            Arrays.stream(responseArray.getBody()).forEach(System.out::println);


        // "/api/v1/customers/{id}"
        Map<String, String> params = new HashMap<>();
        params.put("id", "205");
        ResponseEntity<Customer> responseEntity = restTemplate.getForEntity(URI_CUSTOMERS_ID, Customer.class, params);

        if(responseEntity.getStatusCode().equals(HttpStatus.OK))
            System.out.println("customer = " + responseEntity.getBody());


    }

}
