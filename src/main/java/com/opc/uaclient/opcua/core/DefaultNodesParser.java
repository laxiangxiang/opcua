package com.opc.uaclient.opcua.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认使用的节点解析器。
 */
public class DefaultNodesParser implements NodesParser {

    @Override
    public List<String> getNodesList(String nodes, int plcNo) {
        /**
         * @param node
         * @return
         * 将对应的OpcUa配置属性,解析成为服务OpcUa列表规则列表
         */
        List<String> nodeList=new ArrayList<>();
        if(nodes !=null || nodes.contains("NodeBase")){
            String[]  machineNoList=new String[0];
            String[] strList = nodes.split("-");
            String  nodeBase=strList[0].trim().substring(strList[0].indexOf("#")+1).trim();
            if (nodes.contains("No#")) {
                machineNoList = strList[1].trim().substring(strList[1].trim().indexOf("#") + 1).split(",");
            }
            String[] subVarList = strList[strList.length-1].trim().substring(strList[strList.length - 1].trim().indexOf("#") + 1).split(",");
            if( machineNoList.length != 0 ) {
                for(String machineNo : machineNoList) {
                    for(String subvar : subVarList) {
                        nodeList.add(nodeBase + machineNo.trim() + "." + subvar.trim());
                    }
                }
            } else {
                for(String subvar : subVarList) {
                    nodeList.add(nodeBase + "." + subvar.trim());
                }
            }
        }
        return nodeList;
    };
}
