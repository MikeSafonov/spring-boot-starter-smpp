package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.github.mikesafonov.smpp.core.sender.exceptions.SenderClientBindException;
import com.github.mikesafonov.smpp.core.sender.exceptions.SmppSessionException;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class DefaultSenderClientTest extends BaseDefaultSenderClientTest {

    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class,
                () -> new DefaultSenderClient(randomTransmitterConfiguration(), null, randomInt(), randomBoolean(), randomInt(), new MessageBuilder(new DefaultTypeOfAddressParser())));
        assertThrows(NullPointerException.class,
                () -> new DefaultSenderClient(null, new DefaultSmppClient(), randomInt(), randomBoolean(), randomInt(), new MessageBuilder(new DefaultTypeOfAddressParser())));

        assertThrows(NullPointerException.class,
                        () -> new DefaultSenderClient(randomTransmitterConfiguration(), new DefaultSmppClient(), randomInt(), randomBoolean(), randomInt(), null));

    }

    @Test
    void shouldContainExpectedId() {
        assertEquals(transmitterConfiguration.getName(), senderClient.getId());
    }

    @Test
    void shouldThrowSenderClientBindExceptionWhenSetupFailed() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {

        when(smppClient.bind(transmitterConfiguration)).thenThrow(SmppSessionException.class);

        String message = assertThrows(SenderClientBindException.class, () -> senderClient.setup()).getMessage();
        assertEquals(format("Unable to bind with configuration: %s ", transmitterConfiguration.configInformation()), message);
    }

    @Test
    void shouldSuccessSetup() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);

        when(smppClient.bind(transmitterConfiguration)).thenReturn(session);

        assertDoesNotThrow(() -> senderClient.setup());

        senderClient.setup();

        verify(smppClient, times(1)).bind(transmitterConfiguration);
    }
}
