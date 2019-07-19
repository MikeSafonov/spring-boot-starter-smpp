package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.github.mikesafonov.smpp.config.SmppProperties;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mike Safonov
 */
class DefaultResponseClientTest {
    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new DefaultResponseClient(null, new DefaultSmppClient(), 10));
        assertThrows(NullPointerException.class, () -> new DefaultResponseClient(randomReceiverConfiguration(), null, 10));
    }

    @Test
    void shouldContainExpectedId() {

        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();

        DefaultResponseClient responseClient = new DefaultResponseClient(receiverConfiguration, new DefaultSmppClient(), randomInt());

        assertEquals(receiverConfiguration.getName(), responseClient.getId());
    }

    private ReceiverConfiguration randomReceiverConfiguration(){
        SmppProperties.Credentials credentials = new SmppProperties.Credentials();
        credentials.setHost(randomIp());
        credentials.setPort(randomPort());
        credentials.setUsername(randomString());
        credentials.setPassword(randomString());

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(credentials);
        smsc.setLoggingBytes(false);
        smsc.setLoggingPdu(false);

        return new ReceiverConfiguration(randomString(), smsc.getCredentials(), smsc.getLoggingBytes(), smsc.getLoggingPdu());
    }
}
