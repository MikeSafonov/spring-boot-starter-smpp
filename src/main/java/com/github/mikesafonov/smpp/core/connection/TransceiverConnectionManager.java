package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.impl.DefaultSmppClient;

/**
 * Configuration for sender session
 *
 * @author Mike Safonov
 */
public class TransceiverConnectionManager extends BaseSenderConnectionManager {
    public TransceiverConnectionManager(DefaultSmppClient client,
                                        TransceiverConfiguration configuration,
                                        SmppSessionHandler sessionHandler,
                                        int maxTryCount) {
        super(client, configuration, sessionHandler, maxTryCount);
    }
}
