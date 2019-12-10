package com.opc.uaclient;

import com.opc.uaclient.opcua.core.OpcUaConfiguration2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

    /**
     * 使用配置入口类2的配置方式
     * @return
     */
//    @Bean
//    public OpcUaConfiguration2 opcUaConfiguration2(){
//        return new OpcUaConfiguration2();
//    }
}
