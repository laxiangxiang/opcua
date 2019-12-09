package com.opc.uaclient.opcua.exception;


/**
 * @author fujun
 */
public class OpcUaClientException extends Exception {
    public OpcUaClientException() {
        super();
    }

    public OpcUaClientException(String message) {
        super(message);
    }

    public OpcUaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
