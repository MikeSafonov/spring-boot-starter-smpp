package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.SmppSession;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link ResponseClient}.
 *
 * @author Mike Safonov
 */
@Slf4j
public class StandardResponseClient implements ResponseClient {
    private final ConnectionManager connectionManager;
    private boolean inited = false;

    /**
     * @param connectionManager smpp connection manager
     */
    public StandardResponseClient(@NotNull ConnectionManager connectionManager) {
        this.connectionManager = requireNonNull(connectionManager);
    }

    @Override
    public @NotNull String getId() {
        return connectionManager.getConfiguration().getName();
    }

    @Override
    public void setup() {
        if (!inited) {
            connectionManager.getSession();
            inited = true;
        }
    }

    @Override
    public void reconnect() {
        connectionManager.closeSession();
        connectionManager.getSession();
    }

    @Override
    public void destroyClient() {
        connectionManager.destroy();
    }

    @Override
    public SmppSession getSession() {
        return connectionManager.getSession();
    }
}
