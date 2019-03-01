package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class DefaultSenderManager implements SenderManager {

    private final List<SenderClient> senderClients;


    @Override
    public SenderClient getClient() {
        return null;
    }
}
