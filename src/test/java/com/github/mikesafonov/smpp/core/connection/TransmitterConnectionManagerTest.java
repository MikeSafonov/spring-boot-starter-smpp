package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.github.mikesafonov.smpp.core.exceptions.SmppException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.randomPositive;
import static com.github.mikesafonov.smpp.util.Randomizer.randomTransmitterConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TransmitterConnectionManagerTest {
    private DefaultSmppClient smppClient;
    private TransmitterConfiguration configuration;
    private int maxTryCount;
    private TransmitterConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        smppClient = mock(DefaultSmppClient.class);
        configuration = randomTransmitterConfiguration();
        maxTryCount = randomPositive(5);
        connectionManager = new TransmitterConnectionManager(smppClient, configuration, maxTryCount);
    }

    @Nested
    class GetSession {
        @Test
        @SneakyThrows
        void shouldCreateSession() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration)).thenReturn(session);

            SmppSession smppSession = connectionManager.getSession();

            assertEquals(session, smppSession);
        }

        @Test
        @SneakyThrows
        void shouldReturnCurrentSessionWhenPingIsOk() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration)).thenReturn(session);
            when(session.isBound()).thenReturn(true);
            when(session.enquireLink(any(EnquireLink.class), anyLong())).thenReturn(new EnquireLinkResp());

            connectionManager.getSession();
            SmppSession smppSession = connectionManager.getSession();

            assertEquals(session, smppSession);
        }

        @Test
        @SneakyThrows
        void shouldReconnectWhenPingIsFails() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration)).thenReturn(session);
            when(session.isBound()).thenReturn(true);
            when(session.enquireLink(any(EnquireLink.class), anyLong())).thenThrow(RuntimeException.class);

            connectionManager.getSession();
            connectionManager.getSession();

            verify(session).close();
            verify(session).destroy();
        }

        @Test
        @SneakyThrows
        void shouldReconnectWhenNotBoundAndNotBinding() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration)).thenReturn(session);
            when(session.isBound()).thenReturn(false);
            when(session.isBinding()).thenReturn(false);

            connectionManager.getSession();
            connectionManager.getSession();

            verify(session).close();
            verify(session).destroy();
        }

        @Test
        @SneakyThrows
        void shouldThrowSmppSessionExceptionBecauseUnableToConnect() {
            when(smppClient.bind(configuration)).thenThrow(RuntimeException.class);

            assertThrows(SmppException.class, () -> connectionManager.getSession());

            verify(smppClient, times(maxTryCount)).bind(configuration);
        }
    }

    @Nested
    class CloseSession {
        @Test
        @SneakyThrows
        void shouldCloseSession() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration)).thenReturn(session);

            connectionManager.getSession();
            connectionManager.destroy();
            verify(session).close();
            verify(session).destroy();
        }

        @Test
        @SneakyThrows
        void shouldDoNothingBecauseSessionIsNull() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration)).thenReturn(session);

            connectionManager.destroy();

            verifyNoInteractions(session);
        }
    }

    @Nested
    class Destroy {
        @Test
        void shouldDestroyClient() {
            connectionManager.destroy();

            verify(smppClient).destroy();
        }

        @Test
        @SneakyThrows
        void shouldCloseSession() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration)).thenReturn(session);

            connectionManager.getSession();
            connectionManager.destroy();
            verify(session).close();
            verify(session).destroy();
        }
    }
}
