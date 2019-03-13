package com.github.mikesafonov.starter.smpp.reciever;

import com.cloudhopper.smpp.SmppSession;
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
     * @param sessionHandler handler for listening PDU events (delivery reports, etc)
     * @throws ResponseClientBindException if connection not successfully
     */
    void setup(ResponseSmppSessionHandler sessionHandler);

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
     * Check is response client in 'in process' state. This status means what response client handing
     * some pdu request at the moment
     *
     * @return current 'in process' status
     */
    boolean isInProcess();

    /**
     * Change client 'in process' status
     *
     * @param inProcess 'in process' status
     */
    void setInProcess(boolean inProcess);

    /**
     * Close smpp session connection and destroy client
     */
    void destroyClient();
}
