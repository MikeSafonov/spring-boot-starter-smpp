package com.github.mikesafonov.starter.clients;


import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mike Safonov
 */
public interface SmppResultGenerator {

    @NotNull MessageResponse generate(@NotBlank String smscId, @NotNull Message message);
}
