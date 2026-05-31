package com.alpha.first_spring.service;

import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    
    public String helloWorld(String name) {

        return "F.U.N " + name;
    }
}
