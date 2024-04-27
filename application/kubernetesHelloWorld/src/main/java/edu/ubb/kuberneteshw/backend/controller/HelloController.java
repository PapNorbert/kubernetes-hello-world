package edu.ubb.kuberneteshw.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/hello")
public class HelloController {

    @GetMapping()
    public String getMessage() {
        System.out.println("Get request at /hello" );
        return "Hello World! This is a demo application version 1";
    }
}
