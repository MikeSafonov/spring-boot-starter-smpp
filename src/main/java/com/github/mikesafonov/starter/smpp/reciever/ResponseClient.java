package com.github.mikesafonov.starter.smpp.reciever;

import com.cloudhopper.smpp.SmppSession;

import javax.validation.constraints.NotNull;

/**
 * @author Mike Safonov
 */
public interface ResponseClient {

    @NotNull String getId();

    void setup(ResponseSmppSessionHandler sessionHandler) throws ResponseClientBindException;

    SmppSession getSession();

    void reconnect();

    boolean isInProcess();

    void setInProcess(boolean inProcess);

    void destroyClient();
}
