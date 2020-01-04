package com.github.mikesafonov.smpp;

import com.github.mikesafonov.smpp.api.SenderManager;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoundRobinApplicationService {
    private final SenderManager senderManager;

    public void sendMessage(String from, String to, String text) {
        Message message = Message.simple(text)
                .from(from)
                .to(to)
                .build();
        SenderClient client = senderManager.getClient().get();
        client.send(message);
    }
}
