package com.opc.uaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class UaclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(UaclientApplication.class, args);
    }

    @RequestMapping("/hello")
    public String helloWorld(){
        return "hello world!";
    }

}
