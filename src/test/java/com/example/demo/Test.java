package com.example.demo;

import com.example.demo.opcua.core.OpcUaProperties;
import com.example.demo.opcua.util.YamlConverter;
import com.example.demo.opcua.util.YamlReader;

public class Test {

    @org.junit.Test
    public void yamlReaderTest(){
        Object o = YamlReader.getInstance().read("opcua.yml");
        if (o != null){
            System.out.println(o);
        }else {
            System.out.println("null");
        }
    }
    
    @org.junit.Test
    public void yamlConvertorTest(){
        OpcUaProperties opcUaProperties = YamlConverter.getInstance().readAndConvert("opcua.yml",OpcUaProperties.class);
        System.out.println(opcUaProperties);
    }
}
