package com.opc.uaclient.opcua.core;

import java.util.List;

/**
 * 节点解析器，可以根据自己的配置实现自己的解析器
 */
public interface NodesParser {
    List<String> getNodesList(String nodes, int plcNo);
}
