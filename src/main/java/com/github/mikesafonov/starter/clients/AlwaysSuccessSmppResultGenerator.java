package com.github.mikesafonov.starter.clients;


import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Implementation of {@link SmppResultGenerator} which always generate success {@link MessageResponse} with
 * random smsc message id.
 *
 * @author Mike Safonov
 */
public class AlwaysSuccessSmppResultGenerator implements SmppResultGenerator {

    @Override
    public MessageResponse generate(String smscId, Message message) {
        return MessageResponse.success(message, smscId, randomHexId());
    }


    private String randomHexId() {
        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return Long.toHexString(timestamp);
    }
}
