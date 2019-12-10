package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.exception.OpcUaClientException;
import com.opc.uaclient.opcua.pojo.Relation;
import com.opc.uaclient.opcua.pojo.UaClientPOJO;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.opcfoundation.ua.transport.security.SecurityMode;
import sun.font.DelegatingShape;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        int sessionTimeOut = Integer.valueOf(plc.get("sessionTimeOut"));
        //安全模式
        String securityMode = plc.get("securityMode");
        //用户认证模式
        String userAuthenticationMode = plc.get("userAuthenticationMode");
        boolean isConnect = Boolean.valueOf(plc.get("isConnect"));
        boolean isSubscribe = Boolean.valueOf(plc.get("isSubscribe"));
        UaClient uaClient;
        try {
            uaClient=new UaClient(address);
            setSecurityMode(uaClient,securityMode);
            setUserAuthenticationMode(uaClient,userAuthenticationMode,username,password);
            uaClient.setAutoReconnect(true);
//            uaClient.setListener(new DefaultUaClientListener());
            uaClient.setKeepSubscriptions(true);
            uaClient.setSessionName("plc:"+plcNo);
            uaClient.setSessionTimeout(sessionTimeOut, TimeUnit.DAYS);
        } catch (Exception e ) {
            log.info("client create failed:{},exception:{}",address,e.getMessage());
            return;
        }
        UaClientPOJO pojo = new UaClientPOJO(plcNo,ns,isConnect,isSubscribe,uaClient);
        relation.getUaClientPOJOS().add(pojo);
        log.info("client create success:{}",address);
    }

    /**
     * 设置安全模式
     * @param uaClient
     * @param securityMode
     */
    private static void setSecurityMode(UaClient uaClient,String securityMode){
        if (StringUtils.isBlank(securityMode) || securityMode.equalsIgnoreCase("NONE")){
            uaClient.setSecurityMode(SecurityMode.NONE);
        }else if (securityMode.equalsIgnoreCase("BASIC128RSA15_SIGN_ENCRYPT")){
            uaClient.setSecurityMode(SecurityMode.BASIC128RSA15_SIGN_ENCRYPT);
        }else if (securityMode.equalsIgnoreCase("BASIC128RSA15_SIGN")){
            uaClient.setSecurityMode(SecurityMode.BASIC128RSA15_SIGN);
        }else if (securityMode.equalsIgnoreCase("BASIC256_SIGN_ENCRYPT")){
            uaClient.setSecurityMode(SecurityMode.BASIC256SHA256_SIGN_ENCRYPT);
        }else if (securityMode.equalsIgnoreCase("BASIC256_SIGN")){
            uaClient.setSecurityMode(SecurityMode.BASIC256_SIGN);
        }else if (securityMode.equalsIgnoreCase("BASIC256SHA256_SIGN_ENCRYPT")){
            uaClient.setSecurityMode(SecurityMode.BASIC256SHA256_SIGN_ENCRYPT);
        }else if (securityMode.equalsIgnoreCase("BASIC256SHA256_SIGN")){
            uaClient.setSecurityMode(SecurityMode.BASIC256SHA256_SIGN);
        }
    }

    /**
     * 设置认证模式
     * TODO 实现相应认证模式
     * @param uaClient
     * @param userAuthenticationMode
     * @param username
     * @param password
     * @throws Exception
     */
    private static void setUserAuthenticationMode(UaClient uaClient,String userAuthenticationMode,String username,String password) throws Exception{
        if (StringUtils.isBlank(userAuthenticationMode) || userAuthenticationMode.equalsIgnoreCase("Anonymous")){
            return;
        }else if (userAuthenticationMode.equalsIgnoreCase("UserName")){
            if(!StringUtils.isBlank(username)  && !StringUtils.isBlank(password)){
                UserIdentity userIdentity = new UserIdentity(username, password);
                uaClient.setUserIdentity(userIdentity);
            }else {
                throw new OpcUaClientException("在用户名认证模式下，请配置用户名和密码。");
            }
        }else if (userAuthenticationMode.equalsIgnoreCase("Certificate")){

        }else if (userAuthenticationMode.equalsIgnoreCase("IssuedToken")){

        }else if (userAuthenticationMode.equalsIgnoreCase("Kerberos")){

        }
    }
}
