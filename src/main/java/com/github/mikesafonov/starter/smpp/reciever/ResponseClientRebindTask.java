package com.github.mikesafonov.starter.smpp.reciever;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Runnable class for checking and reconnecting {@link ResponseClient}
 *
 * @author Mike Safonov
 */
public class ResponseClientRebindTask implements Runnable {

    private final ResponseClient client;

    public ResponseClientRebindTask(@NotNull ResponseClient client) {
        this.client = requireNonNull(client);
    }

    /**
     * Is {@link #client}`s session is null then try to reconnect by calling {@link ResponseClient#reconnect()}
     */
    @Override
    public void run() {
        if (client.getSession() == null) {
            client.reconnect();
            return;
        }
        if (client.getSession().isBound()) {
            if (!client.isInProcess()) {
                client.reconnect();
            }
            return;
        }
        if (client.getSession().isBinding()) {
            return;
        }
        client.reconnect();
    }

}
