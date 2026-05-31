package com.alpha.first_spring.controller;

import org.springframework.web.bind.annotation.RestController;

import com.alpha.first_spring.service.HelloWorldService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/hello-world")
public class HelloWorldController {

    private HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;

    }
    @GetMapping
    public String helloWorld() {
        return helloWorldService.helloWorld("Alpha");
    }
}
