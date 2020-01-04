package com.github.mikesafonov.smpp.core.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageBuilderTest {
    private String text = "text";
    private String phone = "phone";
    private String source = "source";
    private String messageId = "message_id";

    @Test
    void shouldBuildSilentMessage() {
        Message message = Message.silent(text)
                .from(source)
                .to(phone)
                .messageId(messageId)
                .build();

        assertTrue(message.isSilent());
        assertEquals(MessageType.SILENT, message.getMessageType());
        assertEquals(text, message.getText());
        assertEquals(phone, message.getMsisdn());
        assertEquals(source, message.getSource());
        assertEquals(messageId, message.getMessageId());
    }

    @Test
    void shouldBuildDatagramMessage() {
        Message message = Message.datagram(text)
                .from(source)
                .to(phone)
                .messageId(messageId)
                .build();
        assertEquals(MessageType.DATAGRAM, message.getMessageType());
        assertEquals(text, message.getText());
        assertEquals(phone, message.getMsisdn());
        assertEquals(source, message.getSource());
        assertEquals(messageId, message.getMessageId());
    }

    @Test
    void shouldBuildSimpleMessage() {
        Message message = Message.simple(text)
                .from(source)
                .to(phone)
                .messageId(messageId)
                .build();
        assertEquals(MessageType.SIMPLE, message.getMessageType());
        assertEquals(text, message.getText());
        assertEquals(phone, message.getMsisdn());
        assertEquals(source, message.getSource());
        assertEquals(messageId, message.getMessageId());
    }
}
