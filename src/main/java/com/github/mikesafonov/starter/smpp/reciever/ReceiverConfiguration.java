package com.github.mikesafonov.starter.smpp.reciever;

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

    public ReceiverConfiguration(@NotNull String name, @NotNull SmppProperties.Credentials credentials, boolean loggingBytes, boolean loggingPdu) {
        super();

        setType(SmppBindType.RECEIVER);
        setName(name);
        setHost(credentials.getHost());
        setPort(credentials.getPort());
        setSystemId(credentials.getUsername());
        setPassword(credentials.getPassword());
        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(loggingBytes);
        loggingOptions.setLogPdu(loggingPdu);
        setLoggingOptions(loggingOptions);

    }

    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s", getName(), getHost(), getPort(), getSystemId());
    }
}
