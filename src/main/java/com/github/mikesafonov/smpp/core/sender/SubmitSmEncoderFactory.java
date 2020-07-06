package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.dto.Message;

/**
 * Factory class for {@link SubmitSmEncoder}
 *
 * @author Mike Safonov
 */
public class SubmitSmEncoderFactory {

    public SubmitSmEncoder get(Message message) {
        switch (message.getMessageType()) {
            case SIMPLE:
            case DATAGRAM:
                return new SimpleSubmitSmEncoder();
            case SILENT:
                return new SilentSubmitSmEncoder();
            case FLASH:
                return new FlashSubmitSmEncoder();
            default:
                throw new RuntimeException("Unable to find encoder for message type " + message.getMessageType());
        }
    }
}
