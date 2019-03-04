package com.github.mikesafonov.starter.clients;


import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Implementations of this intreface is used by mock {@link MockSenderClient} or test {@link TestSenderClient} sender
 * clients to generate request answer {@link MessageResponse}
 *
 * @author Mike Safonov
 */
public interface SmppResultGenerator {

    @NotNull MessageResponse generate(@NotBlank String smscId, @NotNull Message message);
}
