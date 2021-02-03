package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.CancelMessageResponse;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link SenderClient} which should be used for testing purpose. This client
 * may provide real smpp connection via incoming implementation of {@link SenderClient}. Every incoming request will
 * be redirected to real {@link #senderClient} only if list of allowed phone numbers {@link #allowedPhones}
 * contains message destination phone. Otherwise {@link MessageResponse}/{@link CancelMessageResponse}
 * will be generated via {@link SmppResultGenerator}
 *
 * @author Mike Safonov
 * @author Mikhail Epatko
 */
public class TestSenderClient implements SenderClient {

    /**
     * List of allowed phones to real smpp actions
     */
    private final List<String> allowedPhones;
    /**
     * Real smpp sender client
     */
    private final SenderClient senderClient;
    /**
     * Generator for {@link MessageResponse}
     */
    private final SmppResultGenerator smppResultGenerator;

    public TestSenderClient(@NotNull SenderClient senderClient, @NotNull List<String> allowedPhones,
                            @NotNull SmppResultGenerator smppResultGenerator) {
        this.senderClient = requireNonNull(senderClient);
        this.allowedPhones = new ArrayList<>(allowedPhones);
        this.smppResultGenerator = requireNonNull(smppResultGenerator);
    }

    @Override
    public @NotNull String getId() {
        return senderClient.getId();
    }

    @Override
    public void setup() {
        senderClient.setup();
    }

    @Override
    public MessageResponse send(Message message) {
        if (isAllowed(message.getMsisdn())) {
            return senderClient.send(message);
        }
        return smppResultGenerator.generate(senderClient.getId(), message);
    }

    @Override
    public @NotNull CancelMessageResponse cancel(@NotNull CancelMessage cancelMessage) {
        if (isAllowed(cancelMessage.getMsisdn())) {
            return senderClient.cancel(cancelMessage);
        }
        return smppResultGenerator.generate(senderClient.getId(), cancelMessage);
    }

    @Override
    public Optional<ConnectionManager> getConnectionManager() {
        return senderClient.getConnectionManager();
    }

    private boolean isAllowed(String phone) {
        return allowedPhones.contains(phone);
    }

    public List<String> getAllowedPhones() {
        return allowedPhones;
    }
}
