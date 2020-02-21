package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppBindType;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.util.Randomizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransceiverConfigurationTest {

    @Test
    void shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TransceiverConfiguration(null,
                new SmppProperties.Credentials(), true, true, 1));
        assertThrows(NullPointerException.class, () -> new TransceiverConfiguration("asdasd",
                null, true, true, 1));
    }

    @Test
    void shouldCreateCorrectConfiguration() {
        String name = Randomizer.randomString();
        SmppProperties.Credentials credentials = Randomizer.randomCredentials();
        boolean loggingBytes = Randomizer.randomBoolean();
        boolean loggingPdu = Randomizer.randomBoolean();
        int windowsSize = Randomizer.randomInt();
        TransceiverConfiguration configuration =
                new TransceiverConfiguration(name, credentials, loggingBytes, loggingPdu, windowsSize);

        assertEquals(name, configuration.getName());
        assertEquals(credentials.getHost(), configuration.getHost());
        assertEquals(credentials.getPassword(), configuration.getPassword());

        assertEquals(credentials.getPort(), configuration.getPort());
        assertEquals(credentials.getUsername(), configuration.getSystemId());
        assertEquals(SmppBindType.TRANSCEIVER, configuration.getType());
        assertEquals(windowsSize, configuration.getWindowSize());
        assertEquals(loggingBytes, configuration.getLoggingOptions().isLogBytesEnabled());
        assertEquals(loggingPdu, configuration.getLoggingOptions().isLogPduEnabled());
        assertEquals(String.format("%s host=%s port=%d username=%s windowsSize=%d", name, credentials.getHost(),
                credentials.getPort(), credentials.getUsername(), windowsSize),
                configuration.configInformation());

    }
}
