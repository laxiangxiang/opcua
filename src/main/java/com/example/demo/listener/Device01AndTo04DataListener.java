package com.example.demo.listener;

import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;

public class Device01AndTo04DataListener implements MonitoredDataItemListener {

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue dataValue, DataValue dataValue1) {

    }
}
