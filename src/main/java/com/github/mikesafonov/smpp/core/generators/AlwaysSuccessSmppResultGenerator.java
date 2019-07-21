package com.github.mikesafonov.smpp.core.generators;


import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.CancelMessageResponse;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Implementation of {@link SmppResultGenerator} which always generate success {@link MessageResponse}/{@link CancelMessageResponse} with
 * random smsc message id (random UUID).
 *
 * @author Mike Safonov
 */
@EqualsAndHashCode
public class AlwaysSuccessSmppResultGenerator implements SmppResultGenerator {

    @Override
    public MessageResponse generate(String smscId, Message message) {
        return MessageResponse.success(message, smscId, randomHexId());
    }

    @Override
    public @NotNull CancelMessageResponse generate(@NotNull String smscId, @NotNull CancelMessage cancelMessage) {
        return CancelMessageResponse.success(cancelMessage, smscId);
    }


    private String randomHexId() {
        return UUID.randomUUID().toString();
    }
}
