package com.opc.uaclient.opcua.appcontext;

import com.opc.uaclient.opcua.core.OpcUaConfiguration2;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author shdq-fjy
 */
public class SpringbootOnReady implements ApplicationListener<ApplicationStartedEvent> {

    private OpcUaConfiguration2 opcUaConfiguration;

    public SpringbootOnReady(OpcUaConfiguration2 opcUaConfiguration) {
        this.opcUaConfiguration = opcUaConfiguration;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        opcUaConfiguration.startConn();
    }
}
