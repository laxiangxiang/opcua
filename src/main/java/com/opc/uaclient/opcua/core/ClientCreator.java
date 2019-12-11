package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.exception.OpcUaClientException;
import com.opc.uaclient.opcua.pojo.Relation;
import com.opc.uaclient.opcua.pojo.UaClientPOJO;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.UaClient;
import com.sun.org.apache.xpath.internal.operations.Mod;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.opcfoundation.ua.transport.security.SecurityMode;
import sun.font.DelegatingShape;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fujun
 */
@Slf4j
public class ClientCreator {

    private static Relation relation = Relation.getInstance();

    public static void create(Map<String,String> plc) throws Exception{
        String address = plc.get("address");
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
        uaClient=new UaClient(address);
        setSecurityMode(uaClient,securityMode);
        setUserAuthenticationMode(uaClient,userAuthenticationMode,plc);
        uaClient.setAutoReconnect(true);
//       uaClient.setListener(new DefaultUaClientListener());
        uaClient.setKeepSubscriptions(true);
        uaClient.setSessionName("plc:"+plcNo);
        uaClient.setSessionTimeout(sessionTimeOut, TimeUnit.DAYS);
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
        if (StringUtils.isBlank(securityMode) || securityMode.equalsIgnoreCase(Mode.SecurityMode.NONE.getName())){
            uaClient.setSecurityMode(SecurityMode.NONE);
        }else if (securityMode.equalsIgnoreCase(Mode.SecurityMode.BASIC128RSA15_SIGN_ENCRYPT.getName())){
            uaClient.setSecurityMode(SecurityMode.BASIC128RSA15_SIGN_ENCRYPT);
        }else if (securityMode.equalsIgnoreCase(Mode.SecurityMode.BASIC128RSA15_SIGN.getName())){
            uaClient.setSecurityMode(SecurityMode.BASIC128RSA15_SIGN);
        }else if (securityMode.equalsIgnoreCase(Mode.SecurityMode.BASIC256_SIGN_ENCRYPT.getName())){
            uaClient.setSecurityMode(SecurityMode.BASIC256_SIGN_ENCRYPT);
        }else if (securityMode.equalsIgnoreCase(Mode.SecurityMode.BASIC256_SIGN.getName())){
            uaClient.setSecurityMode(SecurityMode.BASIC256_SIGN);
        }else if (securityMode.equalsIgnoreCase(Mode.SecurityMode.BASIC256SHA256_SIGN_ENCRYPT.getName())){
            uaClient.setSecurityMode(SecurityMode.BASIC256SHA256_SIGN_ENCRYPT);
        }else if (securityMode.equalsIgnoreCase(Mode.SecurityMode.BASIC256SHA256_SIGN.getName())){
            uaClient.setSecurityMode(SecurityMode.BASIC256SHA256_SIGN);
        }
    }

    /**
     * 设置认证模式
     * @param uaClient
     * @param userAuthenticationMode
     * @throws Exception
     */
    private static void setUserAuthenticationMode(UaClient uaClient,String userAuthenticationMode,Map<String,String> plc) throws Exception{
        UserIdentity userIdentity;
        if (StringUtils.isBlank(userAuthenticationMode) || userAuthenticationMode.equalsIgnoreCase(Mode.UserAuthenticationMode.Anonymous.getName())){
            return;
        }else if (userAuthenticationMode.equalsIgnoreCase(Mode.UserAuthenticationMode.UserName.getName())){
            String username = plc.get("username");
            String password = plc.get("password");
            if(!StringUtils.isBlank(username)  && !StringUtils.isBlank(password)){
                userIdentity = new UserIdentity(username, password);
                uaClient.setUserIdentity(userIdentity);
            }else {
                throw new OpcUaClientException("在用户名认证模式下，请配置用户名和密码。");
            }
        }else if (userAuthenticationMode.equalsIgnoreCase(Mode.UserAuthenticationMode.Certificate.getName())){
            String certificateFileOrURL = plc.get("certificateFileOrURL");
            String privateKeyFileOrURL = plc.get("privateKeyFileOrURL");
            String privateKeyPassword = plc.get("privateKeyPassword");
            if (!StringUtils.isBlank(certificateFileOrURL) || !StringUtils.isBlank(privateKeyFileOrURL) || !StringUtils.isBlank(privateKeyPassword)){
                if (isUrl(certificateFileOrURL) && isUrl(privateKeyFileOrURL)){
                    URL certificateURL = new URL(certificateFileOrURL);
                    URL privateKeyURL = new URL(privateKeyFileOrURL);
                    userIdentity = new UserIdentity(certificateURL,privateKeyURL,privateKeyPassword);
                }else {
                    File certificateFile = new File(certificateFileOrURL);
                    File privateKeyFile = new File(privateKeyFileOrURL);
                    userIdentity = new UserIdentity(certificateFile,privateKeyFile,privateKeyPassword);
                }
                uaClient.setUserIdentity(userIdentity);
            }else {
                throw new OpcUaClientException("在证书认证模式下，请配置证书文件路径或者URL");
            }
        }else if (userAuthenticationMode.equalsIgnoreCase(Mode.UserAuthenticationMode.IssuedToken.getName())){
            return;
        }else if (userAuthenticationMode.equalsIgnoreCase(Mode.UserAuthenticationMode.Kerberos.getName())){
            return;
        }
    }

    private static boolean isUrl(String url) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";

        Pattern pattern = Pattern.compile(regex.trim());
        Matcher matcher = pattern.matcher(url.trim());
        isurl = matcher.matches();
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }
}
