package com.opc.uaclient.opcua.clientlistener;

import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.application.Session;
import org.opcfoundation.ua.core.PublishRequest;
import org.opcfoundation.ua.core.PublishResponse;

/**
 *默认的客户端监听器，客户端向本客户端订阅的所有节点发送请求，并接受响应
 * 请求速率由配置文件中的publishRate决定
 * @author shdq-fjy
 */
@Slf4j
public class DefaultUaClientListener extends UaClientListener{
    /**
     * 会话创建成功后调用，只会调用一次
     * //TODO
     * @param uaClient
     * @param session
     */
    @Override
    public void onAfterCreateSession(UaClient uaClient, Session session) {
        log.debug("{}:----onAfterCreateSession----",uaClient.getUri());
    }

    /**
     * 在每次发送请求前调用，每隔publishRate时间调用一次
     * @param uaClient
     * @param request
     */
    @Override
    public void onBeforeRequest(UaClient uaClient, PublishRequest request) {
        log.debug("{} ----onBeforeRequest----",uaClient.getUri());
    }

    /**
     * 接收每次请求后的响应,可以做校验或者过滤工作
     * @param uaClient
     * @param response
     * @return
     */
    @Override
    public boolean validateResponse(UaClient uaClient, PublishResponse response) {
        log.debug("{} ----validateResponse----",uaClient.getUri());
        return true;
    }
}
