package com.github.mikesafonov.starter.smpp.config;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.type.LoggingOptions;

/**
 * Configuration for receiver session
 *
 * @author Mike Safonov
 */
public class ReceiverConfiguration extends SmppSessionConfiguration {

    public ReceiverConfiguration(String host, int port, String username, String password, boolean isLoggingBytes, boolean isLoggingPdu) {

        super();

        setType(SmppBindType.RECEIVER);
        setHost(host);
        setPort(port);
        setSystemId(username);
        setPassword(password);
        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(isLoggingBytes);
        loggingOptions.setLogPdu(isLoggingPdu);
        setLoggingOptions(loggingOptions);

    }


    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s", "Receiver", getHost(), getPort(), getSystemId());
    }
}
