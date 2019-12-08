package com.example.demo.opcua.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 有关监听器与订阅节点关系集合的存储类
 * @author fujun
 */
@Data
public class Relation {

    private List<UaClientPOJO> uaClientPOJOS = new ArrayList<>();

    //监听器名与订阅节点列表的关系map
    private List<ListenerPOJO> listenerPOJOS = new ArrayList<>();

    private Relation() {
    }

    private static class SingleHolder{
        private static Relation instance = new Relation();
    }

    public static Relation getInstance (){
        return SingleHolder.instance;
    }
}
