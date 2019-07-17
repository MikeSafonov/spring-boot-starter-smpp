package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.type.LoggingOptions;
import com.github.mikesafonov.smpp.config.SmppProperties;

import javax.validation.constraints.NotNull;

/**
 * Configuration for sender session
 *
 * @author Mike Safonov
 */
public class TransmitterConfiguration extends SmppSessionConfiguration {

    public TransmitterConfiguration(@NotNull String name, @NotNull SmppProperties.Credentials credentials, boolean loggingBytes,
                                    boolean loggingPdu, int windowsSize) {
        super();

        setType(SmppBindType.TRANSMITTER);
        setName(name);
        setHost(credentials.getHost());
        setPort(credentials.getPort());
        setSystemId(credentials.getUsername());
        setPassword(credentials.getPassword());
        setWindowSize(windowsSize);

        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(loggingBytes);
        loggingOptions.setLogPdu(loggingPdu);
        setLoggingOptions(loggingOptions);
    }

    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s windowsSize=%d", getName(), getHost(), getPort(), getSystemId(), getWindowSize());
    }
}
