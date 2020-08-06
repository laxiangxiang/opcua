package com.opc.uaclient.listener;

import com.opc.uaclient.opcua.util.OpcUaUtil;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;

import java.util.List;

/**
 * @author shdq-fjy
 */
public class ScannerListener implements MonitoredDataItemListener {

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue dataValue, DataValue dataValue1) {
        if(!OpcUaUtil.isNewNodeValueValid("1",monitoredDataItem.getNodeId(),dataValue,dataValue1)){
            return;
        }
        List<Double> doubles = (List<Double>) dataValue.getValue().getValue();
        StringBuffer barcode = new StringBuffer();
        doubles.forEach(d ->{
            barcode.append(d);
        });
    }
}
