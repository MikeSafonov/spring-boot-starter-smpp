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

    public TransmitterConfiguration(String host, int port, String username, String password, int windowSize, boolean isLoggingBytes, boolean isLoggingPdu) {

        super();

        setType(SmppBindType.TRANSMITTER);
        setHost(host);
        setPort(port);
        setSystemId(username);
        setPassword(password);
        setWindowSize(windowSize);
        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(isLoggingBytes);
        loggingOptions.setLogPdu(isLoggingPdu);
        setLoggingOptions(loggingOptions);

    }


    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s windowsSize=%d", "Transmitter", getHost(), getPort(), getSystemId(), getWindowSize());
    }
}
