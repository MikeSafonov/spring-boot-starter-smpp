package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.impl.DefaultSmppClient;

public class TransmitterConnectionManager extends BaseSenderConnectionManager {
    public TransmitterConnectionManager(DefaultSmppClient client,
                                        TransmitterConfiguration configuration,
                                        int maxTryCount) {
        super(client, configuration, maxTryCount);
    }
}
