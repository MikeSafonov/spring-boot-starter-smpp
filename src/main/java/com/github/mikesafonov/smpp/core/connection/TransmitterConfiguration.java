package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.type.LoggingOptions;
import com.github.mikesafonov.smpp.config.SmppProperties;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Configuration for sender session
 *
 * @author Mike Safonov
 */
public class TransmitterConfiguration extends BaseSmppSessionConfiguration {

    public TransmitterConfiguration(@NotNull String name, @NotNull SmppProperties.Credentials credentials,
                                    boolean loggingBytes, boolean loggingPdu, int windowsSize) {
        super();

        setType(SmppBindType.TRANSMITTER);
        setName(requireNonNull(name));
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
        return String.format("%s host=%s port=%d username=%s windowsSize=%d", getName(), getHost(),
                getPort(), getSystemId(), getWindowSize());
    }
}
