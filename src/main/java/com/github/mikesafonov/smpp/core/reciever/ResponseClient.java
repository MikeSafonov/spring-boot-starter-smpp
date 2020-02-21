package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.SmppSession;
import com.github.mikesafonov.smpp.core.exceptions.ResponseClientBindException;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * This abstraction represent connection via SMPP with RECEIVER type. Key purpose is listening
 * delivery reports.
 *
 * @author Mike Safonov
 */
public interface ResponseClient {

    /**
     * @return Identifier of response client
     */
    @NotNull String getId();

    /**
     * Create smpp connection and bind to it
     *
     * @throws ResponseClientBindException if connection not successfully
     */
    void setup();

    /**
     * Access to SMPP session
     *
     * @return smpp session, may be null
     */
    @Nullable
    SmppSession getSession();

    /**
     * Perform reconnection
     */
    void reconnect();

    /**
     * Close smpp session connection and destroy client
     */
    void destroyClient();
}
