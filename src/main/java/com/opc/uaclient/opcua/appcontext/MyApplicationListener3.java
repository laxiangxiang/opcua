package com.opc.uaclient.opcua.appcontext;

import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author shdq-fjy
 */
//@Component
public class MyApplicationListener3 implements ApplicationListener<ApplicationStartedEvent> {
    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("MyApplicationListener3 start");
        Thread.sleep(5000);
        System.out.println("MyApplicationListener3 end");
    }
}
