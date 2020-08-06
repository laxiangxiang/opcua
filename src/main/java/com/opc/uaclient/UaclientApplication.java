package com.opc.uaclient;

import com.opc.uaclient.opcua.appcontext.MyApplicationListener1;
import com.opc.uaclient.opcua.appcontext.MyApplicationListener2;
import com.opc.uaclient.opcua.appcontext.MyApplicationListener3;
import com.opc.uaclient.opcua.appcontext.SpringbootOnReady;
import com.opc.uaclient.opcua.core.OpcUaConfiguration2;
import com.opc.uaclient.opcua.core.OpcUaConfiguration3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shdq-fjy
 */
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
     * 使用配置入口类2的配置方式，需要配置SpringbootOnReady
     * @return
     */
//    @Bean
//    public OpcUaConfiguration2 opcUaConfiguration2(){
//        return new OpcUaConfiguration2();
//    }
//
//    @Bean
//    public SpringbootOnReady springbootOnReady(OpcUaConfiguration2 opcUaConfiguration){
//        return new SpringbootOnReady(opcUaConfiguration);
//    }

    @Bean
    public MyApplicationListener3 myApplicationListener3(){
        return new MyApplicationListener3();
    }

    /**
     * 使用配置入口类3的配置方式
     * @return
     */
    @Bean
    public OpcUaConfiguration3 opcUaConfiguration3(){
        return new OpcUaConfiguration3();
    }

}
