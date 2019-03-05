package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link SenderManager} which return {@link SenderClient} based on {@link IndexDetectionStrategy}
 * implementation
 *
 * @author Mike Safonov
 */
public class StrategySenderManager extends BaseSenderManager {

    private final IndexDetectionStrategy indexDetectionStrategy;

    public StrategySenderManager(@NotNull List<SmscConnection> smscConnections, @NotNull IndexDetectionStrategy indexDetectionStrategy) {
        super(smscConnections);
        this.indexDetectionStrategy = requireNonNull(indexDetectionStrategy);
    }

    @Override
    public Optional<SenderClient> getClient() {
        if (isEmpty()) {
            return Optional.empty();
        }

        if (isSingleton()) {
            return Optional.of(smscConnections.get(0).getSenderClient());
        }

        return Optional.of(smscConnections.get(indexDetectionStrategy.next(size())).getSenderClient());

    }
}
