package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSession;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseClientRebindTask implements Runnable {
    private SmppSession session;
    private SessionReconnector reconnector;

    @Override
    public void run() {
        if (session == null) {
            reconnector.reconnect();
            return;
        }
        if (session.isBound()) {
            reconnector.reconnect();
            return;
        }
        if (session.isBinding()) {
            return;
        }
        reconnector.reconnect();
    }
}
