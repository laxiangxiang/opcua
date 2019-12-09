package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.exception.OpcUaClientException;
import com.opc.uaclient.opcua.pojo.UaClientPOJO;
import com.prosysopc.ua.client.UaClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * opcua客户端端连接服务端连接器
 * 一个客户端对应一个connector
 * @author fujun
 */
@Slf4j
@Data
public class Connector implements Runnable{

    private UaClientPOJO uaClientPOJO;

    private Subscriber subscriber;

    private boolean isConnected = false;

    public Connector( UaClientPOJO uaClientPOJO, Subscriber subscriber) {
        this.uaClientPOJO = uaClientPOJO;
        this.subscriber = subscriber;
    }

    public Connector(UaClientPOJO uaClientPOJO) {
        this.uaClientPOJO = uaClientPOJO;
    }

    @Override
    public void run() {
        backendConn();
    }

    private boolean backendConn(){
        if(uaClientPOJO.isConnect()){
            if (uaClientPOJO.isSubscribe()){
                //如果是要连接并且需要订阅监听节点，选择订阅方法，防止重复连接。
                return listene();
            }else{
                //如果是连接但是不订阅监听节点，选择connect方法，连接plc ua服务即可
                return connect();
            }
        }
        return false;
    }

//    private void connectAlwaysInBackend() throws Throwable{
//        if(uaClientPOJO.isConnect()){
//            retryTemplate.execute(
//                    new RetryCallback<Boolean, Throwable>() {
//                        @Override
//                        public Boolean doWithRetry(RetryContext context) throws Throwable {
//                            log.info("{}:开始第 {} 次连接。。。",uaClientPOJO.getUaClient().getUri(),context.getRetryCount());
//                            if (uaClientPOJO.isSubscribe()){
//                                //如果是要连接并且需要订阅监听节点，选择订阅方法，防止重复连接。
//                                return doListener();
//                            }else{
//                                //如果是连接但是不订阅监听节点，选择connect方法，连接plc ua服务即可
//                                return connect();
//                            }
//                        }
//                    },
//                    new RecoveryCallback<Boolean>() {
//                        @Override
//                        public Boolean recover(RetryContext context) throws Exception {
//                            log.error("{}:{} 次连接失败,停止连接。",uaClientPOJO.getUaClient().getUri(),context.getRetryCount());
//                            return false;
//                        }
//                    });
//        }
//    }

    /**
     * 执行订阅
     */
    private boolean listene()  {
        return subscriber.subscribe(this);
//        return subscriber.connectAndSubscribe(this);
    }

    /**
     * 根据UaClient连接相应OPCUA服务并调用启动订阅的功能
     * @return
     * @throws OpcUaClientException
     */
    protected boolean connect(){
        UaClient uaClient = uaClientPOJO.getUaClient();
        if (!uaClient.isConnected()) {
            log.info("connecting ua server:{}", uaClient.getUri());
            try {
                uaClient.connect();
                log.info("connect success:{}",uaClient.getUri());
                isConnected = true;
                return true;
            } catch (Exception e) {
                log.error("connect ua server failed:{}", uaClient.getUri());
                isConnected = false;
                return false;
            }
        } else {
            isConnected = true;
            return true;
        }
    }
}
