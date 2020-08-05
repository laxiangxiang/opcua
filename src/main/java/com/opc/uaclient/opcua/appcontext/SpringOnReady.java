package com.opc.uaclient.opcua.appcontext;

import com.opc.uaclient.opcua.core.OpcUaConfiguration2;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author shdq-fjy
 */
public class SpringOnReady implements ApplicationListener<ContextRefreshedEvent> {

    private OpcUaConfiguration2 opcUaConfiguration;

    public SpringOnReady(OpcUaConfiguration2 opcUaConfiguration) {
        this.opcUaConfiguration = opcUaConfiguration;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        opcUaConfiguration.startConn();
    }
}
