package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppBindType;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.util.Randomizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransmitterConfigurationTest {

    @Test
    void shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TransmitterConfiguration(null, new SmppProperties.Credentials(), true, true, 1));
        assertThrows(NullPointerException.class, () -> new TransmitterConfiguration("asdasd", null, true, true, 1));
    }

    @Test
    void shouldCreateCorrectConfiguration() {
        String name = Randomizer.randomString();
        SmppProperties.Credentials credentials = Randomizer.randomCredentials();
        boolean loggingBytes = Randomizer.randomBoolean();
        boolean loggingPdu = Randomizer.randomBoolean();
        int windowsSize = Randomizer.randomInt();
        TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(name, credentials, loggingBytes, loggingPdu, windowsSize);

        assertEquals(name, transmitterConfiguration.getName());
        assertEquals(credentials.getHost(), transmitterConfiguration.getHost());
        assertEquals(credentials.getPassword(), transmitterConfiguration.getPassword());

        assertEquals(credentials.getPort(), transmitterConfiguration.getPort());
        assertEquals(credentials.getUsername(), transmitterConfiguration.getSystemId());
        assertEquals(SmppBindType.TRANSMITTER, transmitterConfiguration.getType());
        assertEquals(windowsSize, transmitterConfiguration.getWindowSize());
        assertEquals(loggingBytes, transmitterConfiguration.getLoggingOptions().isLogBytesEnabled());
        assertEquals(loggingPdu, transmitterConfiguration.getLoggingOptions().isLogPduEnabled());
        assertEquals(String.format("%s host=%s port=%d username=%s windowsSize=%d", name, credentials.getHost(),
                credentials.getPort(), credentials.getUsername(), windowsSize),
                transmitterConfiguration.configInformation());

    }
}
