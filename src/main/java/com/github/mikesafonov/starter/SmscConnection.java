package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.reciever.ResponseClient;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
@AllArgsConstructor
public class SmscConnection {
    private final String name;
    private final ResponseClient responseClient;
    private final SenderClient senderClient;

    public SmscConnection(String name, SenderClient senderClient) {
        this.name = name;
        this.senderClient = senderClient;
        this.responseClient = null;
    }
}
