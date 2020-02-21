package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.github.mikesafonov.smpp.core.exceptions.ResponseClientBindException;
import com.github.mikesafonov.smpp.core.reciever.ResponseSmppSessionHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.github.mikesafonov.smpp.util.Randomizer.randomPositive;
import static com.github.mikesafonov.smpp.util.Randomizer.randomReceiverConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReceiverConnectionManagerTest {
    private DefaultSmppClient smppClient;
    private ReceiverConfiguration configuration;
    private ResponseSmppSessionHandler responseSmppSessionHandler;
    private long rebindPeriod;
    private ScheduledExecutorService scheduledExecutorService;
    private ReceiverConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        smppClient = mock(DefaultSmppClient.class);
        configuration = randomReceiverConfiguration();
        responseSmppSessionHandler = mock(ResponseSmppSessionHandler.class);
        rebindPeriod = randomPositive(10);
        scheduledExecutorService = mock(ScheduledExecutorService.class);
        connectionManager = new ReceiverConnectionManager(smppClient, configuration,
                responseSmppSessionHandler, rebindPeriod, scheduledExecutorService);
    }

    @Nested
    class GetSession {
        @Test
        @SneakyThrows
        void shouldCreateSession() {
            SmppSession session = mock(SmppSession.class);
            ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);
            when(scheduledExecutorService.scheduleAtFixedRate(
                    any(ResponseClientRebindTask.class), eq(5L), eq(rebindPeriod), eq(TimeUnit.SECONDS)))
                    .thenReturn(scheduledFuture);

            SmppSession smppSession = connectionManager.getSession();
            assertEquals(session, smppSession);
        }

        @Test
        @SneakyThrows
        void shouldNotCreateNewSessionBecauseSessionNotNull() {
            SmppSession session = mock(SmppSession.class);
            ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);
            when(scheduledExecutorService.scheduleAtFixedRate(
                    any(ResponseClientRebindTask.class), eq(5L), eq(rebindPeriod), eq(TimeUnit.SECONDS)))
                    .thenReturn(scheduledFuture);

            connectionManager.getSession();
            connectionManager.getSession();

            verify(smppClient, times(1)).bind(configuration, responseSmppSessionHandler);
        }

        @Test
        @SneakyThrows
        void shouldThrowSmppSessionExceptionBecauseUnableToConnect() {
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenThrow(SmppTimeoutException.class);

            assertThrows(ResponseClientBindException.class, () -> connectionManager.getSession());
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
        void shouldShutdownExecutor() {
            connectionManager.destroy();

            verify(scheduledExecutorService).shutdown();
        }

        @Test
        @SneakyThrows
        void shouldInterruptTaskIfNotNull() {
            SmppSession session = mock(SmppSession.class);
            ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
            when(smppClient.bind(configuration, responseSmppSessionHandler)).thenReturn(session);
            when(scheduledExecutorService.scheduleAtFixedRate(
                    any(ResponseClientRebindTask.class), eq(5L), eq(rebindPeriod), eq(TimeUnit.SECONDS)))
                    .thenReturn(scheduledFuture);

            connectionManager.getSession();

            connectionManager.destroy();

            verify(scheduledFuture).cancel(true);
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
