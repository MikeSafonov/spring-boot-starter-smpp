package com.github.mikesafonov.starter.clients;

import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import com.github.mikesafonov.starter.smpp.sender.exceptions.SenderClientBindException;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Mike Safonov
 */
public class TestSenderClient implements SenderClient {

    private final List<String> allowedPhones;
    private final SenderClient senderClient;
    private final SmppResultGenerator smppResultGenerator;
    private final String id = UUID.randomUUID().toString();

    public TestSenderClient(SenderClient senderClient, List<String> allowedPhones, SmppResultGenerator smppResultGenerator) {
        this.senderClient = senderClient;
        this.allowedPhones = new ArrayList<>(allowedPhones);
        this.smppResultGenerator = smppResultGenerator;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setup() throws SenderClientBindException {
    }

    @Override
    public MessageResponse send(Message message) {
        if (isAllowed(message.getMsisdn())) {
            return senderClient.send(message);
        }
        return smppResultGenerator.generate(message);
    }

    private boolean isAllowed(String phone) {
        return allowedPhones.contains(phone);
    }
}
