package com.github.mikesafonov.starter.clients;


import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Mike Safonov
 */
public class AlwaysSuccessSmppResultGenerator implements SmppResultGenerator {

    @Override
    public MessageResponse generate(Message message) {
        //TODO: fix
        return MessageResponse.success(message, randomHexId(), null);
    }


    private String randomHexId() {
        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return Long.toHexString(timestamp);
    }
}
