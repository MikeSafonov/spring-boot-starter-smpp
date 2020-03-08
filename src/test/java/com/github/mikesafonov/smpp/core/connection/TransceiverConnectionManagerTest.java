package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.github.mikesafonov.smpp.core.exceptions.SmppException;
import com.github.mikesafonov.smpp.core.reciever.ResponseSmppSessionHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.randomPositive;
import static com.github.mikesafonov.smpp.util.Randomizer.randomTransceiverConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TransceiverConnectionManagerTest {
    private DefaultSmppClient smppClient;
    private TransceiverConfiguration configuration;
    private ResponseSmppSessionHandler responseSmppSessionHandler;
    private int maxTryCount;
    private TransceiverConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        smppClient = mock(DefaultSmppClient.class);
        configuration = randomTransceiverConfiguration();
        maxTryCount = randomPositive(5);
        responseSmppSessionHandler = mock(ResponseSmppSessionHandler.class);
        connectionManager = new TransceiverConnectionManager(smppClient, configuration,
                responseSmppSessionHandler, maxTryCount);
    }

    @Nested
    class GetSession {
        @Test
        @SneakyThrows
        void shouldCreateSession() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);

            SmppSession smppSession = connectionManager.getSession();

            assertEquals(session, smppSession);
        }

        @Test
        @SneakyThrows
        void shouldReturnCurrentSessionWhenPingIsOk() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);
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
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);
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
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);
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
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenThrow(RuntimeException.class);

            assertThrows(SmppException.class, () -> connectionManager.getSession());

            verify(smppClient, times(maxTryCount)).bind(configuration, responseSmppSessionHandler);
        }
    }

    @Nested
    class CloseSession {
        @Test
        @SneakyThrows
        void shouldCloseSession() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);

            connectionManager.getSession();
            connectionManager.destroy();
            verify(session).close();
            verify(session).destroy();
        }

        @Test
        @SneakyThrows
        void shouldDoNothingBecauseSessionIsNull() {
            SmppSession session = mock(SmppSession.class);
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);

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
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);

            connectionManager.getSession();
            connectionManager.destroy();
            verify(session).close();
            verify(session).destroy();
        }
    }
}
