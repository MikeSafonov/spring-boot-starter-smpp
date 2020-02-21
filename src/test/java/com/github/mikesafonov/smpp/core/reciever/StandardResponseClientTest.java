package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.SmppSession;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.connection.ReceiverConfiguration;
import com.github.mikesafonov.smpp.core.exceptions.ResponseClientBindException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.randomReceiverConfiguration;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class StandardResponseClientTest {
    private ConnectionManager connectionManager;
    private StandardResponseClient responseClient;

    @BeforeEach
    void setUp() {
        connectionManager = mock(ConnectionManager.class);
        responseClient = new StandardResponseClient(connectionManager);
    }

    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () ->
                new StandardResponseClient(null));
    }

    @Test
    void shouldContainExpectedId() {

        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(receiverConfiguration);

        assertEquals(receiverConfiguration.getName(), responseClient.getId());
    }

    @Test
    void shouldFailSetup() {
        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();

        when(connectionManager.getSession()).thenThrow(new ResponseClientBindException(format("Unable to bind with configuration: %s ",
                receiverConfiguration.configInformation())));

        ResponseClientBindException exception = assertThrows(ResponseClientBindException.class, responseClient::setup);
        assertEquals(format("Unable to bind with configuration: %s ", receiverConfiguration.configInformation()),
                exception.getMessage());
    }

    @Test
    void shouldSuccessSetup() {
        SmppSession session = mock(SmppSession.class);

        when(connectionManager.getSession()).thenReturn(session);

        responseClient.setup();

        assertEquals(session, responseClient.getSession());
    }

    @Test
    void shouldSuccessReconnect() {
        responseClient.reconnect();

        verify(connectionManager).closeSession();
        verify(connectionManager).getSession();
    }

    @Test
    void shouldDestroy() {
        responseClient.destroyClient();

        verify(connectionManager).destroy();
    }
}
