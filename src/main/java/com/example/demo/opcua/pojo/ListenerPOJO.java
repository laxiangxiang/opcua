package com.example.demo.opcua.pojo;

import com.prosysopc.ua.client.MonitoredDataItemListener;
import lombok.Data;

/**
 * @author fujun
 */
@Data
public class ListenerPOJO {

    private int plcNo;

    private String listenerName;

    private Object subscribedNodes;

    private MonitoredDataItemListener listener;

    public ListenerPOJO(int plcNo, String listenerName, Object subscribedNodes) {
        this.plcNo = plcNo;
        this.listenerName = listenerName;
        this.subscribedNodes = subscribedNodes;
    }
}
