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

    public ReceiverConfiguration(SmppConfigurationProperties configurationProperties) {

        super();

        if (configurationProperties == null) {
            throw new RuntimeException("configurationProperties must not be null");
        }

        setType(SmppBindType.RECEIVER);
        setHost(configurationProperties.getHost());
        setPort(configurationProperties.getPort());
        setSystemId(configurationProperties.getUsername());
        setPassword(configurationProperties.getPassword());
        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(configurationProperties.isLoggingBytes());
        loggingOptions.setLogPdu(configurationProperties.isLoggingPdu());
        setLoggingOptions(loggingOptions);

    }


    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s", "Receiver", getHost(), getPort(), getSystemId());
    }
}
