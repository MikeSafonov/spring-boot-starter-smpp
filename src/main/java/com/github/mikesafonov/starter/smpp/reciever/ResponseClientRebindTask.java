package com.github.mikesafonov.starter.smpp.reciever;

/**
 * Runnable class for checking and reconnecting {@link DefaultResponseClient}
 *
 * @author Mike Safonov
 */
class ResponseClientRebindTask implements Runnable {

    private final ResponseClient client;

    ResponseClientRebindTask(ResponseClient client) {
        this.client = client;
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
