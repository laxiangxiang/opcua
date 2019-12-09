package com.opc.uaclient.parser;

import com.opc.uaclient.opcua.core.NodesParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class MyParser implements NodesParser {

    @Override
    public List<String> getNodesList(String nodes, int plcNo) {
        log.info("加载自己的节点解析器！");
        return Arrays.asList(nodes.split(","));
    }
}
