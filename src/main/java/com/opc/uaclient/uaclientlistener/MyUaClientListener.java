package com.opc.uaclient.uaclientlistener;

import com.opc.uaclient.opcua.clientlistener.UaClientListener;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.application.Session;
import org.opcfoundation.ua.core.PublishRequest;
import org.opcfoundation.ua.core.PublishResponse;

/**
 * 自定义客户端监听器
 */
@Slf4j
public class MyUaClientListener extends UaClientListener {
    @Override
    public void onAfterCreateSession(UaClient uaClient, Session session) {
        log.info("onAfterCreateSession");
    }

    @Override
    public void onBeforeRequest(UaClient uaClient, PublishRequest request) {
        log.info("onBeforeRequest");
    }

    @Override
    public boolean validateResponse(UaClient uaClient, PublishResponse response) {
        log.info("validateResponse");
        return true;
    }
}
