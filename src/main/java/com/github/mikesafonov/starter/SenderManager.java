package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author Mike Safonov
 */
public interface SenderManager {

    @NotNull
    Optional<SenderClient> getByName(@NotBlank String name);

    @NotNull
    SenderClient getClient();
}
