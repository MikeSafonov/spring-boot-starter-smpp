package com.github.mikesafonov.starter.smpp.config;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.type.LoggingOptions;
import com.github.mikesafonov.starter.SmppProperties;

import javax.validation.constraints.NotNull;

/**
 * Configuration for receiver session
 *
 * @author Mike Safonov
 */
public class ReceiverConfiguration extends SmppSessionConfiguration {

    public ReceiverConfiguration(@NotNull SmppProperties.SMSC smsc) {
        super();

        setType(SmppBindType.RECEIVER);
        setHost(smsc.getHost());
        setPort(smsc.getPort());
        setSystemId(smsc.getUsername());
        setPassword(smsc.getPassword());
        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(smsc.isLoggingBytes());
        loggingOptions.setLogPdu(smsc.isLoggingPdu());
        setLoggingOptions(loggingOptions);

    }

    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s", "Receiver", getHost(), getPort(), getSystemId());
    }
}
