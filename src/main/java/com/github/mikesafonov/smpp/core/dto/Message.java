package com.github.mikesafonov.smpp.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * Incoming message
 *
 * @author Mike Safonov
 */
@Data
@AllArgsConstructor
public class Message {

    /**
     * Message text
     */
    @NotBlank
    private String text;
    /**
     * Destination phone number(msisdn)
     */
    @NotBlank
    private String msisdn;
    /**
     * Source name (alpha name)
     */
    @NotBlank
    private String source;
    /**
     * Client specific id. May be null
     */
    @Nullable
    private String messageId;

    /**
     * Message type
     */
    @NotNull
    private MessageType messageType;

    public boolean isSilent() {
        return messageType == MessageType.SILENT;
    }

    public static MessageBuilder silent(String text) {
        return new MessageBuilder(text, MessageType.SILENT);
    }

    public static MessageBuilder datagram(String text) {
        return new MessageBuilder(text, MessageType.DATAGRAM);
    }

    public static MessageBuilder simple(String text) {
        return new MessageBuilder(text, MessageType.SIMPLE);
    }

    public static MessageBuilder flash(String text){
        return new MessageBuilder(text, MessageType.FLASH);
    }

    public static class MessageBuilder {
        private final String text;
        private final MessageType messageType;
        private String msisdn;
        private String source;
        private String messageId;

        public MessageBuilder(String text, MessageType messageType) {
            this.text = text;
            this.messageType = messageType;
        }

        public MessageBuilder from(String from) {
            this.source = from;
            return this;
        }

        public MessageBuilder to(String to) {
            this.msisdn = to;
            return this;
        }

        public MessageBuilder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Message build() {
            return new Message(text, msisdn, source, messageId, messageType);
        }
    }
}
