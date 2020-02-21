package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppBindType;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.util.Randomizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReceiverConfigurationTest {

    @Test
    void shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new ReceiverConfiguration(null, new SmppProperties.Credentials(), true, true));
        assertThrows(NullPointerException.class, () ->
                new ReceiverConfiguration("asdasd", null, true, true));
    }

    @Test
    void shouldCreateCorrectConfiguration() {
        String name = Randomizer.randomString();
        SmppProperties.Credentials credentials = Randomizer.randomCredentials();
        boolean loggingBytes = Randomizer.randomBoolean();
        boolean loggingPdu = Randomizer.randomBoolean();
        ReceiverConfiguration receiverConfiguration = new ReceiverConfiguration(name, credentials, loggingBytes, loggingPdu);

        assertEquals(name, receiverConfiguration.getName());
        assertEquals(credentials.getHost(), receiverConfiguration.getHost());
        assertEquals(credentials.getPassword(), receiverConfiguration.getPassword());

        assertEquals(credentials.getPort(), receiverConfiguration.getPort());
        assertEquals(credentials.getUsername(), receiverConfiguration.getSystemId());
        assertEquals(SmppBindType.RECEIVER, receiverConfiguration.getType());
        assertEquals(loggingBytes, receiverConfiguration.getLoggingOptions().isLogBytesEnabled());
        assertEquals(loggingPdu, receiverConfiguration.getLoggingOptions().isLogPduEnabled());
        assertEquals(String.format("%s host=%s port=%d username=%s", name, credentials.getHost(),
                credentials.getPort(), credentials.getUsername()),
                receiverConfiguration.configInformation());

    }
}
