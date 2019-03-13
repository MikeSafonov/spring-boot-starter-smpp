package com.github.mikesafonov.starter.smpp;

import com.github.mikesafonov.starter.SmppProperties;
import com.github.mikesafonov.starter.smpp.reciever.DefaultResponseClient;
import com.github.mikesafonov.starter.smpp.reciever.ReceiverConfiguration;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.starter.smpp.util.Randomizer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mike Safonov
 */
class DefaultResponseClientTest {
    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new DefaultResponseClient(null, 10));
    }

    @Test
    void shouldContainExpectedId() {
        SmppProperties.Credentials credentials = new SmppProperties.Credentials();
        credentials.setHost(randomIp());
        credentials.setPort(randomPort());
        credentials.setUsername(randomString());
        credentials.setPassword(randomString());

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(credentials);
        smsc.setLoggingBytes(false);
        smsc.setLoggingPdu(false);

        ReceiverConfiguration receiverConfiguration = new ReceiverConfiguration(randomString(), smsc.getCredentials(), smsc.getLoggingBytes(), smsc.getLoggingPdu());

        DefaultResponseClient responseClient = new DefaultResponseClient(receiverConfiguration, randomInt());

        assertEquals(receiverConfiguration.getName(), responseClient.getId());
    }
}
