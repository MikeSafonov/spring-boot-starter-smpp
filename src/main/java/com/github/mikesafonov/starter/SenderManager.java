package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

/**
 * @author Mike Safonov
 */
public interface SenderManager {

    Optional<SenderClient> getByName(@NotBlank String name);

    Optional<SenderClient> getClient();
}
