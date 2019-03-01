package com.github.mikesafonov.starter.smpp.config;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.type.LoggingOptions;

/**
 * Configuration for sender session
 *
 * @author Mike Safonov
 */
public class TransmitterConfiguration extends SmppSessionConfiguration {

    public TransmitterConfiguration(SmppConfigurationProperties configurationProperties) {

        super();

        if (configurationProperties == null) {
            throw new RuntimeException("configurationProperties must not be null");
        }

        setType(SmppBindType.TRANSMITTER);
        setHost(configurationProperties.getHost());
        setPort(configurationProperties.getPort());
        setSystemId(configurationProperties.getUsername());
        setPassword(configurationProperties.getPassword());
        setWindowSize(configurationProperties.getWindowSize());
        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(configurationProperties.isLoggingBytes());
        loggingOptions.setLogPdu(configurationProperties.isLoggingPdu());
        setLoggingOptions(loggingOptions);

    }


    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s windowsSize=%d", "Transmitter", getHost(), getPort(), getSystemId(), getWindowSize());
    }
}
