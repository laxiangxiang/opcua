package com.opc.uaclient.opcua.pojo;

import com.prosysopc.ua.client.UaClient;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fujun
 */
@Data
@AllArgsConstructor
public class UaClientPOJO {
    private int plcNo;

    private int ns;

    private boolean isConnect;

    private boolean isSubscribe;

    private UaClient uaClient;
}
