package com.github.mikesafonov.starter.clients;

import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class MockSenderClient implements SenderClient {

    private final SmppResultGenerator smppResultGenerator;
    private final String id;


    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setup() {
    }

    @Override
    public @NotNull MessageResponse send(@NotNull Message message) {
        return smppResultGenerator.generate(getId(), message);
    }

}
