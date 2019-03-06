package com.github.mikesafonov.starter.clients;

import com.github.mikesafonov.starter.smpp.dto.CancelMessage;
import com.github.mikesafonov.starter.smpp.dto.CancelMessageResponse;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link SenderClient} which not perform any connection via smpp and only generate {@link MessageResponse}/{@link CancelMessageResponse}
 * by using {@link SmppResultGenerator}
 *
 * @author Mike Safonov
 */
public class MockSenderClient implements SenderClient {

    private final SmppResultGenerator smppResultGenerator;
    private final String id;

    public MockSenderClient(@NotNull SmppResultGenerator smppResultGenerator, @NotNull String id) {
        this.smppResultGenerator = requireNonNull(smppResultGenerator);
        this.id = requireNonNull(id);
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setup() {
    }

    @Override
    public @NotNull MessageResponse send(@NotNull Message message) {
        return smppResultGenerator.generate(id, message);
    }

    @Override
    public @NotNull CancelMessageResponse cancel(@NotNull CancelMessage cancelMessage) {
        return smppResultGenerator.generate(id, cancelMessage);
    }

}
