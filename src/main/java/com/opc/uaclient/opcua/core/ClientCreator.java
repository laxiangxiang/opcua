package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.pojo.Relation;
import com.opc.uaclient.opcua.pojo.UaClientPOJO;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.opcfoundation.ua.transport.security.SecurityMode;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author fujun
 */
@Slf4j
public class ClientCreator {

    private static Relation relation = Relation.getInstance();

    public static void create(Map<String,String> plc){
        String address = plc.get("address");
        String username = plc.get("username");
        String password = plc.get("password");
        int plcNo = Integer.valueOf(plc.get("plcNo"));
        int ns = Integer.valueOf(plc.get("ns"));
        boolean isConnect = Boolean.valueOf(plc.get("isConnect"));
        boolean isSubscribe = Boolean.valueOf(plc.get("isSubscribe"));
        UaClient uaClient;
        try {
            uaClient=new UaClient(address);
            uaClient.setSecurityMode(SecurityMode.NONE);
            //  uaClient.setSessionName(getUaAddress()+">>>"+index++);
            //uaClient.setSessionTimeout(1, TimeUnit.DAYS);
            if(!StringUtils.isBlank(username)  && !StringUtils.isBlank(password)){
                UserIdentity userIdentity = new UserIdentity(username, password);
                uaClient.setUserIdentity(userIdentity);
            }
            uaClient.setAutoReconnect(true);
            uaClient.setListener(new DefaultUaClientListener());
            uaClient.setKeepSubscriptions(true);
            uaClient.setSessionName("plc:"+String.valueOf(plcNo));
        } catch (URISyntaxException | SessionActivationException e ) {
            log.info("client create failed:{},exception:{}",address,e.getMessage());
            return;
        }
        UaClientPOJO pojo = new UaClientPOJO(plcNo,ns,isConnect,isSubscribe,uaClient);
        relation.getUaClientPOJOS().add(pojo);
        log.info("client create success:{}",address);
    }
}
