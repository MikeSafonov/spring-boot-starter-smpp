package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.connection.TransmitterConfiguration;
import com.github.mikesafonov.smpp.core.exceptions.SenderClientBindException;
import com.github.mikesafonov.smpp.core.exceptions.SmppSessionException;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class StandardSenderClientTest extends BaseStandardSenderClientTest {

    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class,
                () -> new StandardSenderClient(connectionManager, randomBoolean(), randomInt(), null));
        assertThrows(NullPointerException.class,
                () -> new StandardSenderClient(null, randomBoolean(), randomInt(), new MessageBuilder(new DefaultTypeOfAddressParser())));
    }

    @Test
    void shouldContainExpectedId() {
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();
        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);

        assertEquals(transmitterConfiguration.getName(), senderClient.getId());
    }

    @Test
    void shouldThrowSenderClientBindExceptionWhenSetupFailed() {
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenThrow(SmppSessionException.class);

        String message = assertThrows(SenderClientBindException.class, () -> senderClient.setup()).getMessage();
        assertEquals(format("Unable to bind with configuration: %s ", transmitterConfiguration.configInformation()), message);
    }

    @Test
    void shouldSuccessSetup() {
        senderClient.setup();

        verify(connectionManager).getSession();
    }
}
