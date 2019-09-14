package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.github.mikesafonov.smpp.core.exceptions.ResponseClientBindException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class StandardResponseClientTest {
    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new StandardResponseClient(null, new DefaultSmppClient(), 10, Executors.newSingleThreadScheduledExecutor()));
        assertThrows(NullPointerException.class, () -> new StandardResponseClient(randomReceiverConfiguration(), null, 10, Executors.newSingleThreadScheduledExecutor()));
        assertThrows(NullPointerException.class, () -> new StandardResponseClient(randomReceiverConfiguration(), new DefaultSmppClient(), 10, null));
    }

    @Test
    void shouldContainExpectedId() {

        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();

        StandardResponseClient responseClient = new StandardResponseClient(receiverConfiguration, new DefaultSmppClient(), randomInt(), Executors.newSingleThreadScheduledExecutor());

        assertEquals(receiverConfiguration.getName(), responseClient.getId());
    }

    @Test
    void shouldFailSetup() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();
        DefaultSmppClient smppClient = mock(DefaultSmppClient.class);
        ResponseSmppSessionHandler handler = mock(ResponseSmppSessionHandler.class);
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);

        when(smppClient.bind(receiverConfiguration, handler)).thenThrow(SmppChannelException.class);

        StandardResponseClient responseClient = new StandardResponseClient(receiverConfiguration, smppClient, randomInt(), executorService);


        ResponseClientBindException exception = assertThrows(ResponseClientBindException.class, () -> responseClient.setup(handler));
        assertEquals(format("Unable to bind with configuration: %s ", receiverConfiguration.configInformation()),
                exception.getMessage());
    }

    @Test
    void shouldSuccessSetup() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();
        DefaultSmppClient smppClient = mock(DefaultSmppClient.class);
        ResponseSmppSessionHandler handler = mock(ResponseSmppSessionHandler.class);
        SmppSession session = mock(SmppSession.class);
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        long rebindPeriod = randomLong();

        when(smppClient.bind(receiverConfiguration, handler)).thenReturn(session);

        StandardResponseClient responseClient = new StandardResponseClient(receiverConfiguration, smppClient, rebindPeriod, executorService);


        responseClient.setup(handler);
        responseClient.setup(handler);

        assertFalse(responseClient.isInProcess());
        assertEquals(session, responseClient.getSession());
        verify(smppClient, times(1)).bind(receiverConfiguration, handler);
        verify(executorService, times(1)).scheduleAtFixedRate(any(ResponseClientRebindTask.class), eq(5L), eq(rebindPeriod), eq(TimeUnit.SECONDS));
    }

    @Test
    void shouldSuccessReconnect() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();
        DefaultSmppClient smppClient = mock(DefaultSmppClient.class);
        ResponseSmppSessionHandler handler = mock(ResponseSmppSessionHandler.class);
        SmppSession session = mock(SmppSession.class);
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        long rebindPeriod = randomLong();

        when(smppClient.bind(receiverConfiguration, handler)).thenReturn(session);

        StandardResponseClient responseClient = new StandardResponseClient(receiverConfiguration, smppClient, rebindPeriod, executorService);

        responseClient.setup(handler);

        responseClient.reconnect();

        assertEquals(session, responseClient.getSession());
        verify(session, times(1)).close();
        verify(session, times(1)).destroy();
        verify(executorService, times(1)).scheduleAtFixedRate(any(ResponseClientRebindTask.class), eq(5L), eq(rebindPeriod), eq(TimeUnit.SECONDS));
        verify(smppClient, times(2)).bind(receiverConfiguration, handler);
    }

    @Test
    void shouldDestroy() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        ReceiverConfiguration receiverConfiguration = randomReceiverConfiguration();
        DefaultSmppClient smppClient = mock(DefaultSmppClient.class);
        ResponseSmppSessionHandler handler = mock(ResponseSmppSessionHandler.class);
        SmppSession session = mock(SmppSession.class);
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        long rebindPeriod = randomLong();
        ScheduledFuture rebindTask = mock(ScheduledFuture.class);

        when(smppClient.bind(receiverConfiguration, handler)).thenReturn(session);
        when(executorService.scheduleAtFixedRate(any(ResponseClientRebindTask.class), eq(5L), eq(rebindPeriod), eq(TimeUnit.SECONDS))).thenReturn(rebindTask);

        StandardResponseClient responseClient = new StandardResponseClient(receiverConfiguration, smppClient, rebindPeriod, executorService);

        responseClient.setup(handler);

        assertEquals(session, responseClient.getSession());
        verify(executorService, times(1)).scheduleAtFixedRate(any(ResponseClientRebindTask.class), eq(5L), eq(rebindPeriod), eq(TimeUnit.SECONDS));

        responseClient.destroyClient();

        assertNull(responseClient.getSession());
        verify(rebindTask, times(1)).cancel(true);
        verify(session, times(1)).close();
        verify(session, times(1)).destroy();
        verify(executorService, times(1)).shutdown();
        verify(smppClient, times(1)).destroy();
    }
}
