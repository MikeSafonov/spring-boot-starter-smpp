package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransmitterConnectionManager extends BaseSenderConnectionManager {
    private static final String SENDER_SUCCESS_BINDED_MESSAGE = "SENDER SUCCESSFUL BINDED";

    public TransmitterConnectionManager(DefaultSmppClient client, TransmitterConfiguration configuration,
                                        int maxTryCount) {
        super(client, configuration, maxTryCount);
    }

    /**
     * Binding new smpp session
     *
     * @return true - if binding was successfully, false - otherwise
     * @see DefaultSmppClient#bind(SmppSessionConfiguration)
     */
    protected boolean bind() {
        try {
            session = client.bind(configuration);
            log.debug(SENDER_SUCCESS_BINDED_MESSAGE);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }
}
