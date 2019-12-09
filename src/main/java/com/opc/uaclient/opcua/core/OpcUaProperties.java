package com.opc.uaclient.opcua.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OpcUaProperties {

    //监听器路径
    private String listenerPath;

    //节点解析器
    private String nodesParser;

    //opcUa 连接属性
    private Retry retry;

    //推送响应速率
    private long publishingRate;

    //对应的opc 站点队列
    private List<Map<String, String>> plcList;

    @Data
    public static class Retry{
        private long connBackOffPeriod = 10000L;
        private int maxAttempts = 3;
        private long backOffPeriod = 1000L;
    }
}
