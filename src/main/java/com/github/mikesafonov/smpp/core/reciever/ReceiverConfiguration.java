package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.type.LoggingOptions;
import com.github.mikesafonov.smpp.config.SmppProperties;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Configuration for receiver session
 *
 * @author Mike Safonov
 */
public class ReceiverConfiguration extends SmppSessionConfiguration {

    public ReceiverConfiguration(@NotNull String name, @NotNull SmppProperties.Credentials credentials,
                                 boolean loggingBytes, boolean loggingPdu) {
        super();

        setType(SmppBindType.RECEIVER);
        setName(Objects.requireNonNull(name));
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
