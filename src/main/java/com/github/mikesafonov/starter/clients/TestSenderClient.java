package com.github.mikesafonov.starter.clients;

import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import com.github.mikesafonov.starter.smpp.sender.exceptions.SenderClientBindException;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link SenderClient} which should be used for testing purpose. This client
 * may provide real smpp connection via incoming implementation of {@link SenderClient}. Every incoming request will be redirected to
 * real {@link #senderClient} only if list of allowed phone numbers {@link #allowedPhones} contains message destination phone.
 * Otherwise {@link MessageResponse} will be generated via {@link SmppResultGenerator}
 *
 * @author Mike Safonov
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

    public TestSenderClient(@NotNull SenderClient senderClient, @NotNull List<String> allowedPhones, @NotNull SmppResultGenerator smppResultGenerator) {
        this.senderClient = requireNonNull(senderClient);
        this.allowedPhones = new ArrayList<>(allowedPhones);
        this.smppResultGenerator = requireNonNull(smppResultGenerator);
    }

    @Override
    public @NotNull String getId() {
        return senderClient.getId();
    }

    @Override
    public void setup() throws SenderClientBindException {
    }

    @Override
    public MessageResponse send(Message message) {
        if (isAllowed(message.getMsisdn())) {
            return senderClient.send(message);
        }
        return smppResultGenerator.generate(senderClient.getId(), message);
    }

    private boolean isAllowed(String phone) {
        return allowedPhones.contains(phone);
    }
}
