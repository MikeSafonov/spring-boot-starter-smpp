package com.github.mikesafonov.smpp.api;

import com.github.mikesafonov.smpp.core.sender.SenderClient;

import javax.validation.constraints.NotBlank;

/**
 * Holder class for sender clients.
 *
 * @author Mike Safonov
 */
public interface SenderManager {

    /**
     * Return sender client based on it name
     *
     * @param name name of sender client
     * @return sender client
     */
    SenderClient getByName(@NotBlank String name);

    /**
     * @return next sender client
     */
    SenderClient getClient();
}
