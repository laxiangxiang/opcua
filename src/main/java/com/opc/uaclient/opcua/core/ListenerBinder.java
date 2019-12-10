package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.exception.OpcUaClientException;
import com.opc.uaclient.opcua.pojo.ListenerPOJO;
import com.opc.uaclient.opcua.pojo.Relation;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 *  把监听器和订阅节点关系做初步绑定
 * @author fujun
 */
@Data
@Slf4j
public class ListenerBinder {

    private static List<String> notListenerNames = Arrays.asList("address","username","password","isConnect","isSubscribe","plcNo","ns","securityMode","userAuthenticationMode","sessionTimeOut");

    private static Relation relation = Relation.getInstance();

    /**
     * 解析订阅的几点，并生成订阅节点列表对应的监听器类名称，做初步绑定。
     * @param properties
     */
    public static void bind(OpcUaProperties  properties) throws OpcUaClientException{
        List<ListenerPOJO> listenerPOJOS = new ArrayList<>();
        NodesParser parser = getParser(properties.getNodesParser());
        if (parser == null){
            throw new OpcUaClientException("获取节点解析器失败。");
        }
        addListenerInstance(properties.getPlcList(),listenerPOJOS,parser,properties.getListenerPath());
        relation.setListenerPOJOS(listenerPOJOS);
    }

    /**
     * 添加节点监听器
     * 根据节点解析中监听器与订阅节点列表的初步绑定关系map，
     * 查找上下文中所有存在并且实现了MonitoredDataItemListener接口的监听器，
     * 自动生成实例并添加。
     */
    private static void addListenerInstance(List<Map<String,String>> plcList, List<ListenerPOJO> listenerPOJOS, NodesParser parser, String listenerPath){
        log.info("请手动编写监听器类！！！");
        //生成监听器名称和监听节点额解析。具体的监听器类需要用户自己实现，类名称为生成的监听器名称。
        for (Map<String,String> plc : plcList){
            int plcNo = Integer.valueOf(plc.get("plcNo"));
            for (Iterator<Map.Entry<String, String>> it = plc.entrySet().iterator(); it.hasNext(); ){
                Map.Entry<String,String> entry = it.next();
                if (notListenerNames.contains(entry.getKey())){
                    it.remove();
                }else {
                    String listenerName = entry.getKey()+"Listener";
                    List<String> subscribedNodes = parser.getNodesList(entry.getValue(),plcNo);
                    ListenerPOJO listenerPOJO = new ListenerPOJO(plcNo, listenerName,subscribedNodes);
                    getListenerInstance(listenerPath,listenerPOJO);
                    listenerPOJOS.add(listenerPOJO);
                    System.err.println("监听器："+listenerName+",订阅节点列表："+subscribedNodes);
                }
            }
        }
    }

    private static void getListenerInstance(String listenerPath, ListenerPOJO pojo) {
            String listenerName = pojo.getListenerName();
            String className = listenerName.substring(0,1).toUpperCase()+listenerName.substring(1);
            try {
                Class clazz = Class.forName(listenerPath+className);
                if (Arrays.asList(clazz.getInterfaces()).contains(MonitoredDataItemListener.class)){
                    MonitoredDataItemListener listener = (MonitoredDataItemListener) clazz.newInstance();
                    pojo.setListener(listener);
                    log.info("监听器：{} 添加成功",className);
                }else {
                    pojo.setListener(null);
                    log.error(className + "没有实现 MonitoredDataItemListener 接口，添加此监听器失败。");
                }
            }catch (Exception e){
                if (e instanceof ClassNotFoundException){
                    pojo.setListener(null);
                    log.error(className +"已在配置文件中配置，但是没有实现类，添加此监听器失败。");
                }else {
                    e.printStackTrace();
                }
            }
    }

    /**
     * 获取解析器
     * @return
     */
    public static NodesParser getParser(String parserName){
        if (parserName == null || parserName.trim().equals("")){
            return new DefaultNodesParser();
        }else {
            try {
                Class clazz = Class.forName(parserName.trim());
                if (Arrays.asList(clazz.getInterfaces()).contains(NodesParser.class)){
                    return (NodesParser) clazz.newInstance();
                }else {
                    log.error("配置的节点解析器必须实现 SubscribeNodesParser 接口。");
                }
            }catch (Exception e){
                if (e instanceof ClassNotFoundException){
                    log.error("配置的节点解析器不存在。");
                }
                e.printStackTrace();
            }
            return null;
        }
    }
}
