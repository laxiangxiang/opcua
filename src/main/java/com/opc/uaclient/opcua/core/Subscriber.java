package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.pojo.ListenerPOJO;
import com.opc.uaclient.opcua.pojo.Relation;
import com.opc.uaclient.opcua.pojo.UaClientPOJO;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅器
 * @author fujun
 */
@Slf4j
public class Subscriber {

    private OpcUaTemplate template;

    private static final Map<String, MonitoredDataItemListener> LISTENER_MAP = new ConcurrentHashMap<>();

    private static Relation relation = Relation.getInstance();

    public Subscriber(OpcUaTemplate template) {
        this.template  = template;
    }

    /**
     * 通过连接器获取当前连接器（客户端对象）所需订阅节点列表和所有监听器
     * @param connector
     */
    protected boolean subscribe(Connector connector) {
        try {
            UaClientPOJO uaClientPOJO = connector.getUaClientPOJO();
            int ns = uaClientPOJO.getNs();
            int plcNo = uaClientPOJO.getPlcNo();
            for (ListenerPOJO listenerPOJO : relation.getListenerPOJOS()) {
                if (plcNo == listenerPOJO.getPlcNo()){
                    MonitoredDataItemListener listener = listenerPOJO.getListener();
                    if (listener == null){
                        log.error("监听器：{} 实例为null，请确保已经编写此类。",listener.getClass().getSimpleName());
                    }else {
                        Object obj = listenerPOJO.getSubscribedNodes();
                        if( obj instanceof List){
                            List<String> strList= (List<String>) obj;
                            for (String nodeIdStr : strList) {
                                if (!LISTENER_MAP.containsKey(nodeIdStr + ":" + listener.getClass().toString())) {
                                    //执行订阅
                                    template.connectAndSubscribe(connector, new NodeId(ns, nodeIdStr), listener);
                                    LISTENER_MAP.put(nodeIdStr + ":" + listener.getClass().toString(), listener);
                                }
                            }
                        }else if(obj  instanceof String){
                            String nodeIdStr= (String) obj;
                            if (!LISTENER_MAP.containsKey(nodeIdStr + ":" + listener.getClass().toString())) {
                                //执行订阅
                                template.connectAndSubscribe(connector, new NodeId(ns, nodeIdStr), listener);
                                LISTENER_MAP.put(nodeIdStr + ":" + listener.getClass().toString(), listener);
                            }
                        }
                        log.info("当前客户端对象uri：{} ，监听器：{} ，订阅节点：{}",uaClientPOJO.getUaClient().getUri(),listener.getClass().getSimpleName(),obj);
                    }

                }
            }
            return true;
        } catch (Exception e) {
            log.error("OpcUa Client Exception when subscribeNodesValue", e);
            return false;
        }
    }

    /**
     * 先连接，再给客户端设置监听和订阅节点
     * 和上一个方法一样
     * @param connector
     * @return
     */
    protected boolean connectAndSubscribe(Connector connector){
        try {
            UaClientPOJO uaClientPOJO = connector.getUaClientPOJO();
            int ns = uaClientPOJO.getNs();
            int plcNo = uaClientPOJO.getPlcNo();
            connector.connect();
            for (ListenerPOJO listenerPOJO : relation.getListenerPOJOS()) {
                if (plcNo == listenerPOJO.getPlcNo()){
                    MonitoredDataItemListener listener = listenerPOJO.getListener();
                    if (listener == null){
                        log.error("监听器：{} 实例为null，请确保已经编写此类。",listener.getClass().getSimpleName());
                    }else {
                        Object obj = listenerPOJO.getSubscribedNodes();
                        if( obj instanceof List){
                            List<String> strList= (List<String>) obj;
                            for (String nodeIdStr : strList) {
                                if (!LISTENER_MAP.containsKey(nodeIdStr + ":" + listener.getClass().toString())) {
                                    //执行订阅
                                    template.subscribe(uaClientPOJO.getUaClient(), new NodeId(ns, nodeIdStr), listener);
                                    LISTENER_MAP.put(nodeIdStr + ":" + listener.getClass().toString(), listener);
                                }
                            }
                        }else if(obj  instanceof String){
                            String nodeIdStr= (String) obj;
                            if (!LISTENER_MAP.containsKey(nodeIdStr + ":" + listener.getClass().toString())) {
                                //执行订阅
                                template.subscribe(uaClientPOJO.getUaClient(), new NodeId(ns, nodeIdStr), listener);
                                LISTENER_MAP.put(nodeIdStr + ":" + listener.getClass().toString(), listener);
                            }
                        }
                        log.info("当前客户端对象uri：{} ，监听器：{} ，订阅节点：{}",uaClientPOJO.getUaClient().getUri(),listener.getClass().getSimpleName(),obj);
                    }

                }
            }
            return true;
        } catch (Exception e) {
            log.error("OpcUa Client Exception when subscribeNodesValue", e);
            return false;
        }
    }

}
