package com.github.mikesafonov.starter.smpp;

import com.github.mikesafonov.starter.SmppProperties;
import com.github.mikesafonov.starter.smpp.reciever.DefaultResponseClient;
import com.github.mikesafonov.starter.smpp.reciever.ReceiverConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mike Safonov
 */
class DefaultResponseClientTest {
    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> DefaultResponseClient.of(null, 10));
    }

    @Test
    void shouldContainExpectedId(){
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setHost("<host>");
        smsc.setPort(1111);
        smsc.setUsername("<username>");
        smsc.setPassword("<password>");

        ReceiverConfiguration receiverConfiguration = new ReceiverConfiguration("res", smsc);

        DefaultResponseClient responseClient = DefaultResponseClient.of(receiverConfiguration, 1000);

        assertEquals(receiverConfiguration.getName(), responseClient.getId());
    }
}
