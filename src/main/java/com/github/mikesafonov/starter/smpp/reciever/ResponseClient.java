package com.github.mikesafonov.starter.smpp.reciever;

import com.cloudhopper.smpp.SmppSession;

/**
 * @author Mike Safonov
 */
public interface ResponseClient {

    void setup(ResponseSmppSessionHandler sessionHandler) throws ResponseClientBindException;

    SmppSession getSession();

    void reconnect();

    boolean isInProcess();

    void setInProcess(boolean inProcess);

    void destroyClient();
}
