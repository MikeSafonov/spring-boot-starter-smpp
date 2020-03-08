package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.github.mikesafonov.smpp.core.reciever.ResponseSmppSessionHandler;

/**
 * Configuration for sender session
 *
 * @author Mike Safonov
 */
public class TransceiverConnectionManager extends BaseSenderConnectionManager {
    public TransceiverConnectionManager(DefaultSmppClient client,
                                        TransceiverConfiguration configuration,
                                        ResponseSmppSessionHandler sessionHandler,
                                        int maxTryCount) {
        super(client, configuration, sessionHandler, maxTryCount);
    }
}
