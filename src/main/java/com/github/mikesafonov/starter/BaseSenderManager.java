package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of {@link SenderManager}
 *
 * @author Mike Safonov
 */
public abstract class BaseSenderManager implements SenderManager {
    protected final List<SmscConnection> smscConnections;

    protected BaseSenderManager(@NotNull List<SmscConnection> smscConnections) {
        this.smscConnections = requireNonNull(smscConnections);
    }

    @Override
    public Optional<SenderClient> getByName(@NotBlank String name) {
        return smscConnections.stream()
                .filter(smscConnection -> smscConnection.getName().equals(name))
                .findFirst()
                .map(SmscConnection::getSenderClient);
    }

    protected boolean isEmpty() {
        return smscConnections.isEmpty();
    }

    protected boolean isSingleton() {
        return smscConnections.size() == 1;
    }

    protected int size() {
        return smscConnections.size();
    }
}
