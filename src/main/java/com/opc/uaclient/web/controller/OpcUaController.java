package com.opc.uaclient.web.controller;

import com.opc.uaclient.opcua.core.OpcUaTemplate;
import com.opc.uaclient.opcua.pojo.Relation;
import com.opc.uaclient.opcua.util.OpcUaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpcUaController {

//    @Autowired
    private OpcUaTemplate template;

    private static Relation relation = Relation.getInstance();

    @RequestMapping("/read")
    public String read(){
        return (String)template.read(2,"6","DataItem_0000");
//        return (String) template.read(2,null,"counter");
    }

    @RequestMapping("/write")
    public void write(){
//        template.write(2,"6","DataItem_0000",0.00099);
        template.write(2,null,"counter",100);
    }

    @RequestMapping("/release")
    public void release(){
        OpcUaUtil.release();
    }

    @RequestMapping("close")
    public void close(){
        template.close();
    }

    @RequestMapping("/connect")
    public void connect(){

    }
}
