package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.github.mikesafonov.smpp.core.exceptions.ResponseClientBindException;
import com.github.mikesafonov.smpp.core.exceptions.SmppSessionException;
import com.github.mikesafonov.smpp.core.reciever.ResponseSmppSessionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class ReceiverConnectionManager implements ConnectionManager {
    private static final String SESSION_SUCCESS_MESSAGE = "SESSION SUCCESSFUL REBINDED";

    private final DefaultSmppClient client;
    private final ReceiverConfiguration configuration;
    private final ResponseSmppSessionHandler sessionHandler;
    /**
     * reconnection period in seconds
     */
    private final long rebindPeriod;
    /**
     * Scheduled reconnection scheduledExecutorService
     */
    private final ScheduledExecutorService scheduledExecutorService;
    /**
     * ScheduledFuture for {@link ResponseClientRebindTask}.
     */
    private ScheduledFuture<?> rebindTask;
    private SmppSession session;


    @Override
    public SmppSession getSession() {
        checkSession();
        return session;
    }

    @Override
    public void closeSession() {
        if (session != null) {
            session.close();
            session.destroy();
            session = null;
        }
    }

    @Override
    public BaseSmppSessionConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void destroy() {
        interruptIfNotNull();
        scheduledExecutorService.shutdown();
        closeSession();
        client.destroy();
    }

    /**
     * Checking smpp session state. If session is null - creating new session using method {@link #bind()}.
     * Otherwise checking bound session state by method {@link #checkBoundState()}.
     *
     * @throws SmppSessionException if session not connected
     */
    private void checkSession() {
        if (session == null) {
            bind();
        }
        if (session == null) {
            throw new ResponseClientBindException(format("Unable to bind with configuration: %s ",
                    configuration.configInformation()));
        }
        setupRebindTask();
    }

    /**
     * Creting SMPP session and connecting to SMSC.
     *
     * @see SmppClient#bind(SmppSessionConfiguration, SmppSessionHandler)
     */
    private void bind() {
        try {
            session = client.bind(configuration, sessionHandler);
            log.debug(SESSION_SUCCESS_MESSAGE);
        } catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            session = null;
        }
    }

    /**
     * Closing existing smpp session and create new connection.
     *
     * @see #closeSession()
     * @see #bind()
     */
    private void reconnect() {
        closeSession();
        bind();
    }

    /**
     * Creating running {@link ResponseClientRebindTask} on single thread {@link ScheduledExecutorService}
     *
     * @see Executors#newSingleThreadScheduledExecutor()
     */
    private void setupRebindTask() {
        interruptIfNotNull();
        rebindTask = scheduledExecutorService.scheduleAtFixedRate(new ResponseClientRebindTask(session, this::reconnect),
                5, rebindPeriod, TimeUnit.SECONDS);
    }

    /**
     * Interrupts {@link #rebindTask} if it not null
     */
    private void interruptIfNotNull() {
        if (rebindTask != null) {
            rebindTask.cancel(true);
        }
    }
}
