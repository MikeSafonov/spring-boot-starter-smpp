package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link ResponseClient}.
 *
 * @author Mike Safonov
 */
@Slf4j
public class StandardResponseClient implements ResponseClient {

    private static final String SESSION_SUCCESS_MESSAGE = "SESSION SUCCESSFUL REBINDED";

    /**
     * Delivery report handler
     */
    private ResponseSmppSessionHandler sessionHandler;
    /**
     * SMPP receiver configuration
     */
    private final ReceiverConfiguration sessionConfiguration;
    /**
     * SMPP client
     */
    private final SmppClient client;
    /**
     * Scheduled reconnection scheduledExecutorService
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * ScheduledFuture for {@link ResponseClientRebindTask}.
     */
    private ScheduledFuture<?> rebindTask;
    /**
     * reconnection period in seconds
     */
    private final long rebindPeriod;
    /**
     * SMPP session.
     */
    private SmppSession session;

    private volatile boolean inProcess = false;
    private boolean inited = false;

    /**
     * Create {@link StandardResponseClient} with {@link DefaultSmppClient}.
     *
     * @param receiverConfiguration    smpp receiver configuration
     * @param client                   {@link DefaultSmppClient}
     * @param rebindPeriod             reconnection period in seconds
     * @param scheduledExecutorService executor service
     */
    public StandardResponseClient(@NotNull ReceiverConfiguration receiverConfiguration,
                                  @NotNull DefaultSmppClient client, long rebindPeriod,
                                  @NotNull ScheduledExecutorService scheduledExecutorService) {
        this.sessionConfiguration = requireNonNull(receiverConfiguration);
        this.client = requireNonNull(client);
        this.scheduledExecutorService = requireNonNull(scheduledExecutorService);
        this.rebindPeriod = rebindPeriod;
    }

    @Override
    public @NotNull String getId() {
        return sessionConfiguration.getName();
    }

    @Override
    public void setup(@NotNull ResponseSmppSessionHandler sessionHandler) {
        if (!inited) {
            this.sessionHandler = sessionHandler;
            bind();
            if (session == null) {
                throw new ResponseClientBindException(format("Unable to bind with configuration: %s ", sessionConfiguration.configInformation()));
            }
            setupRebindTask();
            inited = true;
        }
    }

    /**
     * Closing session by {@link #closeSession()} and bind again by {@link #bind()}
     */
    @Override
    public void reconnect() {
        closeSession();
        bind();
    }

    /**
     * Destroying {@link StandardResponseClient}. Closing session by {@link #closeSession()},
     * destroying smpp client and shutdown {@link #scheduledExecutorService}
     */
    @Override
    public void destroyClient() {
        interruptIfNotNull();
        scheduledExecutorService.shutdown();
        closeSession();
        client.destroy();
    }

    @Override
    public SmppSession getSession() {
        return session;
    }

    @Override
    public boolean isInProcess() {
        return inProcess;
    }

    @Override
    public void setInProcess(boolean inProcess) {
        this.inProcess = inProcess;
    }

    /**
     * Creting SMPP session and connecting to SMSC.
     *
     * @see SmppClient#bind(SmppSessionConfiguration, SmppSessionHandler)
     */
    private void bind() {
        try {
            session = client.bind(sessionConfiguration, sessionHandler);
            log.debug(SESSION_SUCCESS_MESSAGE);
        } catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            session = null;
        }
    }

    /**
     * Creating running {@link ResponseClientRebindTask} on single thread {@link ScheduledExecutorService}
     *
     * @see Executors#newSingleThreadScheduledExecutor()
     */
    private void setupRebindTask() {
        interruptIfNotNull();
        rebindTask = scheduledExecutorService.scheduleAtFixedRate(new ResponseClientRebindTask(this),
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

    /**
     * Closing {@link SmppSession}.
     * Calling {@link SmppSession#close()} and {@link SmppSession#destroy()} if {@link #session} not null
     */
    private void closeSession() {
        if (session != null) {
            session.close();
            session.destroy();
            session = null;
        }
    }

}
