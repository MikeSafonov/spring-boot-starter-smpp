package com.github.mikesafonov.smpp.config;

import com.cloudhopper.smpp.SmppSession;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.reciever.ResponseClient;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

/**
 * This class represent configured smsc connection with {@link SenderClient} and {@link ResponseClient} (optionally)
 *
 * @author Mike Safonov
 */
@Value
@AllArgsConstructor
public class SmscConnection {
    private final String name;
    private final ResponseClient responseClient;
    private final SenderClient senderClient;

    public SmscConnection(String name, SenderClient senderClient) {
        this.name = name;
        this.senderClient = senderClient;
        this.responseClient = null;
    }

    public Optional<ResponseClient> getResponseClient() {
        return Optional.ofNullable(responseClient);
    }

    public void closeSession(){
        senderClient.getConnectionManager().ifPresent(ConnectionManager::destroy);
    }
}
