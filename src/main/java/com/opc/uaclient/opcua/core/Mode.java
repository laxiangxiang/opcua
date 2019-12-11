package com.opc.uaclient.opcua.core;

import com.sun.org.apache.xpath.internal.operations.Mod;

/**
 * @author shdq-fjy
 */
public class Mode {
    private static UserAuthenticationMode userAuthenticationMode;
    private static SecurityMode securityMode;

    public enum SecurityMode{
        NONE("NONE"),
        BASIC128RSA15_SIGN_ENCRYPT("BASIC128RSA15_SIGN_ENCRYPT"),
        BASIC128RSA15_SIGN("BASIC128RSA15_SIGN"),
        BASIC256_SIGN_ENCRYPT("BASIC256_SIGN_ENCRYPT"),
        BASIC256_SIGN("BASIC256_SIGN"),
        BASIC256SHA256_SIGN_ENCRYPT("BASIC256SHA256_SIGN_ENCRYPT"),
        BASIC256SHA256_SIGN("BASIC256SHA256_SIGN");

        private String name;

        SecurityMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum UserAuthenticationMode{
        Anonymous("Anonymous"),
        UserName("UserName"),
        Certificate("Certificate"),
        IssuedToken("IssuedToken"),
        Kerberos("Kerberos");

        private String name;

        UserAuthenticationMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
