package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.reciever.ResponseClient;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class SmscConnection {
    private final String name;
    private final ResponseClient responseClient;
    private final SenderClient senderClient;

}
