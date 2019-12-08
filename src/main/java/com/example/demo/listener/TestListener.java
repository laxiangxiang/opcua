package com.example.demo.listener;

import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;

@Slf4j
public class TestListener implements MonitoredDataItemListener {

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue dataValue, DataValue dataValue1) {
        log.info("my testListener on data change.");
        if (dataValue == null){
            log.info("nodeId:{},oldData:{}",monitoredDataItem.getNodeId(),"null");
        }else {
            log.info("nodeId:{},oldData:{}",monitoredDataItem.getNodeId(),dataValue.getValue().getValue());
        }
        log.info("nodeId:{},newData:{}",monitoredDataItem.getNodeId(),dataValue1.getValue().getValue());
    }
}
