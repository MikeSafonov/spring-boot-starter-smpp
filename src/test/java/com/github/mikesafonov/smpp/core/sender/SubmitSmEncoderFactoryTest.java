package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.dto.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubmitSmEncoderFactoryTest {

    private SubmitSmEncoderFactory factory;

    @BeforeEach
    void setUp(){
        factory = new SubmitSmEncoderFactory();
    }

    @Test
    void shouldReturnSimpleWhenMessageSimple(){
        Message message = Message.simple("").build();

        assertThat(factory.get(message)).isInstanceOf(SimpleSubmitSmEncoder.class);
    }


    @Test
    void shouldReturnSimpleWhenMessageDatagram(){
        Message message = Message.datagram("").build();

        assertThat(factory.get(message)).isInstanceOf(SimpleSubmitSmEncoder.class);
    }


    @Test
    void shouldReturnSilentWhenMessageSilent(){
        Message message = Message.silent("").build();

        assertThat(factory.get(message)).isInstanceOf(SilentSubmitSmEncoder.class);
    }


    @Test
    void shouldReturnFlashWhenMessageFlash(){
        Message message = Message.flash("").build();

        assertThat(factory.get(message)).isInstanceOf(FlashSubmitSmEncoder.class);
    }
}
