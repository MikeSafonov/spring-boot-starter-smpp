package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class DefaultSenderManager implements SenderManager {

    private final List<SmscConnection> smscConnections;


    @Override
    public @NotNull Optional<SenderClient> getByName(@NotBlank String name) {
        return smscConnections.stream()
                .filter(smscConnection -> smscConnection.getName().equals(name))
                .findFirst()
                .map(SmscConnection::getSenderClient);
    }

    @Override
    public SenderClient getClient() {
        return null;
    }
}
