package com.example.demo;

import com.example.demo.opcua.core.OpcUaTemplate;
import com.example.demo.opcua.pojo.ListenerPOJO;
import com.example.demo.opcua.pojo.Relation;
import com.example.demo.opcua.pojo.UaClientPOJO;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RequestMapping("/hello")
    public String helloWorld(){
        return "hello world!";
    }

}
