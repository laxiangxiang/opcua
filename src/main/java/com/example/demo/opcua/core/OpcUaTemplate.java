package com.example.demo.opcua.core;

import com.example.demo.opcua.exception.OpcUaClientException;
import com.example.demo.opcua.pojo.Relation;
import com.example.demo.opcua.pojo.UaClientPOJO;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.*;
import com.prosysopc.ua.nodes.UaDataType;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaVariable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.UnsignedInteger;
import org.opcfoundation.ua.builtintypes.Variant;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.MonitoringMode;
import org.springframework.retry.support.RetryTemplate;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * opcUa 客户端模板，创建客户端实例
 * 并提供客户端连接，配置节点的注册与监听
 */
@Data
@Slf4j
public class OpcUaTemplate {

    private RetryTemplate retryTemplate;

    private OpcUaProperties properties;

    private Relation relation = Relation.getInstance();

    /**
     * 在构造器中创建客户端实例
     * @param opcUaProperties
     */
    public OpcUaTemplate(OpcUaProperties opcUaProperties, RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
        this.properties = opcUaProperties;
    }

    /**
     * 连接OPCUA并执行相应的功能
     * @param uaClientPOJO
     * @param <T>
     * @return
     * @throws OpcUaClientException
     */
    private <T> T execute(UaClientPOJO uaClientPOJO, Supplier<T> supplier)  {
        if (!uaClientPOJO.getUaClient().isConnected()){
            new Connector(uaClientPOJO).connect();
        }
        return supplier.get();
    }

    private <T> T execute(Connector connector, Supplier<T> supplier)  {
        if (!connector.isConnected()){
            connector.connect();
        }
        return supplier.get();
    }

    /**
     * 根据节点ID和监听为相应的节点订阅
     * @param connector
     * @param id
     * @param listener
     * @return
     * @throws OpcUaClientException
     */
    public boolean connectAndSubscribe(Connector connector, NodeId id, MonitoredDataItemListener listener) {
        return execute(connector, () -> doSubscribeNodeValue(connector.getUaClientPOJO().getUaClient(), id, listener));
    }

    /**
     * 在客户端连接后可以调用此方法动态添加监听器
     * @param uaClient
     * @param id
     * @param listener
     * @return
     */
    public boolean subscribe(UaClient uaClient,NodeId id,MonitoredDataItemListener listener){
        return doSubscribeNodeValue(uaClient, id, listener);
    }

    /**
     * 把订阅节点和监听器设置到客户端对象中
     * @param uaClient
     * @param id
     * @param listener
     * @return
     * @throws OpcUaClientException
     */
    private boolean doSubscribeNodeValue(UaClient uaClient, NodeId id, MonitoredDataItemListener listener) {
        try {
            Subscription subscription = new Subscription();
            subscription.setPublishingInterval(properties.getPublishingRate(), TimeUnit.MILLISECONDS);
            MonitoredDataItem item = new MonitoredDataItem(id, Attributes.Value, MonitoringMode.Reporting, subscription.getPublishingInterval());
            item.setDataChangeListener(listener);
            subscription.addItem(item);
            uaClient.addSubscription(subscription);
            return true;
        } catch (ServiceException | StatusException e) {
            log.error("Error add subscribed node {} to listener {}",id ,listener.getClass().getSimpleName());
            return false;
        }
    }

    public void write(int plcNo,String ns,String nodeName,Object value){
        for (UaClientPOJO uaClientPOJO : relation.getUaClientPOJOS()){
            if (uaClientPOJO.getPlcNo() == plcNo){
                if (ns == null){
                    ns = String.valueOf(uaClientPOJO.getNs());
                }
                writeNodeValue(uaClientPOJO,new NodeId(Integer.valueOf(ns),nodeName),value);
            }
        }
    }

    /**
     * 根据UaClient将值写入到指定的节点，写入成功为返回true，否则为false
     * @param uaClientPOJO
     * @param id
     * @param object
     * @return
     * @throws OpcUaClientException
     */
    public boolean writeNodeValue(UaClientPOJO uaClientPOJO, NodeId id, Object object) {
        return execute(uaClientPOJO, () -> doWriteNodeValue(uaClientPOJO, id, object));
    }

    /**
     * 根据UaClient将值写入到指定的节点，写入成功为返回true，否则为false
     * @param uaClientPOJO
     * @param nodeId
     * @param value
     * @return
     * @throws OpcUaClientException
     */
    private boolean doWriteNodeValue(UaClientPOJO uaClientPOJO, NodeId nodeId, Object value) {
        try {
            UnsignedInteger attributeId = Attributes.Value;
            UaClient uaClient = uaClientPOJO.getUaClient();
            UaNode node = uaClient.getAddressSpace().getNode(nodeId);
            UaDataType dataType = null;
            if (attributeId.equals(Attributes.Value) && (node instanceof UaVariable)) {
                UaVariable v = (UaVariable) node;
                if (v.getDataType() == null) {
                    v.setDataType(uaClient.getAddressSpace().getType(v.getDataTypeId()));
                }
                dataType = (UaDataType) v.getDataType();
            }
            // 如果value是数组
            if (value.getClass().isArray()) {
                Object[] array = (Object[]) value;
                Object newArray = null;
                for (int i = 0; i < array.length; i++) {
                    Object el = dataType != null ? uaClient.getAddressSpace().getDataTypeConverter().parseVariant(array[i].toString(), dataType) : value;
                    Object v = ((Variant) el).getValue();
                    if (newArray == null) {
                        newArray = Array.newInstance(v.getClass(), array.length);
                    }
                    Array.set(newArray, i, v);
                }
                return uaClient.writeAttribute(nodeId, attributeId, newArray);
            }
            Object convertedValue = dataType != null ? uaClient.getAddressSpace().getDataTypeConverter().parseVariant(value.toString(), dataType) : value;
            return uaClient.writeAttribute(nodeId, attributeId, convertedValue);
        } catch (AddressSpaceException | ServiceException | StatusException e) {
            log.error("Error writing node value: {}",e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Object read(int plcNo,String ns,String nodeName){
        for (UaClientPOJO uaClientPOJO : relation.getUaClientPOJOS()){
            if (uaClientPOJO.getPlcNo() == plcNo){
                if (ns == null){
                    ns = String.valueOf(uaClientPOJO.getNs());
                }
                Variant variant = readNodeVariant(uaClientPOJO,new NodeId(Integer.valueOf(ns),nodeName));
                return String.valueOf(variant.getValue());
            }
        }
        return "null";
    }
    /**
     * 根据UaClient和节点读取节点值
     * @param id
     * @param uaClientPOJO
     * @return
     * @throws OpcUaClientException
     */
    public Variant readNodeVariant(UaClientPOJO uaClientPOJO, NodeId id) {
        return execute(uaClientPOJO, () -> doReadNodeValue(uaClientPOJO ,id));
    }

    /**
     * 根据UaClient和节点读取节点值
     * @param uaClientPOJO
     * @param id
     * @return
     * @throws OpcUaClientException
     */
    private Variant doReadNodeValue(UaClientPOJO uaClientPOJO, NodeId id) {
        try {
            UaClient uaClient = uaClientPOJO.getUaClient();
            DataValue dataValue = uaClient.readValue(id);
            return dataValue.getValue();
        } catch (ServiceException | StatusException e) {
            log.error("Error reading " + id + " node value: "+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭OPCUA的连接
     */
    public void close() {
        log.info("执行销毁>>>>>>>");
        List<UaClientPOJO> uaClientPOJOS = Relation.getInstance().getUaClientPOJOS();
        for (UaClientPOJO uaClientPOJO : uaClientPOJOS) {
            uaClientPOJO.getUaClient().disconnect();
        }
    }
}
