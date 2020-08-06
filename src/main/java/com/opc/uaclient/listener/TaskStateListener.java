package com.opc.uaclient.listener;

import com.opc.uaclient.opcua.util.OpcUaUtil;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;

/**
 * @author shdq-fjy
 */
public class TaskStateListener implements MonitoredDataItemListener {

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue dataValue, DataValue dataValue1) {
        if(OpcUaUtil.isNewNodeValueValid("1", monitoredDataItem.getNodeId(), dataValue, dataValue1)){
            return;
        }
    }
}
