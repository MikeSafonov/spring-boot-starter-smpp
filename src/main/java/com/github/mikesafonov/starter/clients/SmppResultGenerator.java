package com.github.mikesafonov.starter.clients;


import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;

/**
 * @author Mike Safonov
 */
public interface SmppResultGenerator {

    MessageResponse generate(Message message);
}
