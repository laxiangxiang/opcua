package com.opc.uaclient.opcua.clientlistener;

import com.prosysopc.ua.client.ConnectException;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.application.Session;
import org.opcfoundation.ua.core.PublishRequest;
import org.opcfoundation.ua.core.PublishResponse;
import org.opcfoundation.ua.core.RepublishResponse;

/**
 * 监听了相应客户端订阅的所有的节点
 *
 * @author fujun
 */
@Slf4j
public abstract class UaClientListener implements com.prosysopc.ua.client.UaClientListener {

    /**
     * 客户端与服务端会话创建成功（连接成功）后处理
     * @param uaClient
     * @param session
     * @throws ConnectException
     */
    @Override
    public void onAfterCreateSessionChannel(UaClient uaClient, Session session) throws ConnectException {
        onAfterCreateSession(uaClient, session);
    }

    //下面两个方法与配置文件中publishRate参数有关，参数越小刷新速率越快

    /**
     * 在发送请求前处理
     * 一般不用实现
     * @param uaClient
     * @param publishRequest
     */
    @Override
    public void onBeforePublishRequest(UaClient uaClient, PublishRequest publishRequest) {
        onBeforeRequest(uaClient,publishRequest);
    }

    /**
     * 接收请求的响应，验证响应消息处理
     * @param uaClient
     * @param publishResponse
     * @return
     */
    @Override
    public boolean validatePublishResponse(UaClient uaClient, PublishResponse publishResponse) {
        return validateResponse(uaClient,publishResponse);
    }

    /**
     * 一般不用实现
     * @param uaClient
     * @param republishResponse
     * @return
     */
    @Override
    public boolean validateRepublishResponse(UaClient uaClient, RepublishResponse republishResponse) {
        log.debug("----validateRepublishResponse----");
        return true;
    }

    public abstract void onAfterCreateSession(UaClient uaClient,Session session);

    public abstract void onBeforeRequest(UaClient uaClient,PublishRequest request);

    public abstract boolean validateResponse(UaClient uaClient,PublishResponse response);
}
