package com.example.demo.menuservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MenuController {

    @GetMapping(path="/menu/{type}")
    public String getMenu(@PathVariable("type") String type){
        if("south".equalsIgnoreCase(type)){
            return "Idly, Dosa";
        }else{
            return "Poha, Dhokla";
        }

    }
}
