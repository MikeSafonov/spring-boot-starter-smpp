package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;

/**
 * @author Mike Safonov
 */
public interface SenderManager {
    SenderClient getClient();
}
