package com.example.demo.opcua.core;

import com.prosysopc.ua.client.ConnectException;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.client.UaClientListener;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.application.Session;
import org.opcfoundation.ua.core.PublishRequest;
import org.opcfoundation.ua.core.PublishResponse;
import org.opcfoundation.ua.core.RepublishResponse;

/**
 * @author fujun
 */
@Slf4j
public class DefaultUaClientListener implements UaClientListener {

    @Override
    public void onAfterCreateSessionChannel(UaClient uaClient, Session session) throws ConnectException {
        log.info("onAfterCreateSessionChannel:{}",session.getName());
    }

    @Override
    public void onBeforePublishRequest(UaClient uaClient, PublishRequest publishRequest) {
        log.info("onBeforePublishRequest");
    }

    @Override
    public boolean validatePublishResponse(UaClient uaClient, PublishResponse publishResponse) {
        log.info("validatePublishResponse");
        return true;
    }

    @Override
    public boolean validateRepublishResponse(UaClient uaClient, RepublishResponse republishResponse) {
        log.info("validateRepublishResponse");
        return true;
    }

}
