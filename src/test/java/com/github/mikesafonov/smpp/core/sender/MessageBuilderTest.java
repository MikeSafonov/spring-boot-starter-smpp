package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.CancelSm;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageType;
import com.github.mikesafonov.smpp.core.utils.MessageUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class MessageBuilderTest {

    private static final AddressBuilder DEFAULT_ADDRESS_BUILDER = new AddressBuilder(new DefaultTypeOfAddressParser());

    private AddressBuilder addressBuilder;
    private MessageBuilder messageBuilder;

    private static Stream<Arguments> simpleSubmitSmProvider() {
        return Stream.of(
            simpleMessageWithLatinText(),
            datagramMessageWithLatinText(),
            simpleLargeMessageWithLatinText(),
            simpleMessageWithNonLatinText(),
            silentMessageWithLatinText(),
            silentLargeMessageWithLatinText(),
            flashMessageWithLatinText(),
            flashLargeMessageWithLatinText()
        );
    }

    @SneakyThrows
    private static Arguments simpleMessageWithLatinText() {
        Message message = new Message("test", "33312333213", "test", "test", MessageType.SIMPLE);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        sm.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);
        sm.setShortMessage(CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_GSM));

        return Arguments.of(message, false, sm);
    }

    @SneakyThrows
    private static Arguments datagramMessageWithLatinText() {
        Message message = new Message("test", "33312333213", "test", "test", MessageType.DATAGRAM);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_DATAGRAM);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);
        sm.setShortMessage(CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_GSM));

        return Arguments.of(message, false, sm);
    }

    @SneakyThrows
    private static Arguments simpleLargeMessageWithLatinText() {
        String longText = IntStream.range(0, MessageUtil.GSM_7_REGULAR_MESSAGE_LENGTH + 1).boxed()
            .map(integer -> "A")
            .collect(Collectors.joining());

        Message message = new Message(longText, "33312333213", "test", "test", MessageType.SIMPLE);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());
        byte[] messageByte = CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_GSM);
        Tlv tlv = new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, messageByte);

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        sm.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);
        sm.setShortMessage(new byte[0]);
        sm.addOptionalParameter(tlv);

        return Arguments.of(message, false, sm);
    }

    @SneakyThrows
    private static Arguments simpleMessageWithNonLatinText() {
        Message message = new Message("Привет", "33312333213", "test", "test", MessageType.SIMPLE);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        sm.setDataCoding(SmppConstants.DATA_CODING_UCS2);
        sm.setShortMessage(CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_UCS_2));

        return Arguments.of(message, false, sm);
    }

    @SneakyThrows
    private static Arguments silentMessageWithLatinText() {
        Message message = new Message("test", "33312333213", "test", "test", MessageType.SILENT);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setDataCoding((byte) 0xC0);
        sm.setShortMessage(CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_GSM));

        return Arguments.of(message, false, sm);
    }

    @SneakyThrows
    private static Arguments silentLargeMessageWithLatinText() {
        String longText = IntStream.range(0, MessageUtil.GSM_7_REGULAR_MESSAGE_LENGTH + 1).boxed()
            .map(integer -> "A")
            .collect(Collectors.joining());
        Message message = new Message(longText, "33312333213", "test", "test", MessageType.SILENT);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());

        byte[] messageByte = CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_GSM);
        Tlv tlv = new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, messageByte);

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setDataCoding((byte) 0xC0);
        sm.setShortMessage(new byte[0]);
        sm.addOptionalParameter(tlv);

        return Arguments.of(message, false, sm);
    }

    @SneakyThrows
    private static Arguments flashMessageWithLatinText() {
        Message message = new Message("test", "33312333213", "test", "test", MessageType.FLASH);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_DEFAULT);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setDataCoding((byte) 0x18);
        sm.setShortMessage(CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_UCS_2));

        return Arguments.of(message, false, sm);
    }

    @SneakyThrows
    private static Arguments flashLargeMessageWithLatinText() {
        String longText = IntStream.range(0, MessageUtil.GSM_7_REGULAR_MESSAGE_LENGTH + 1).boxed()
            .map(integer -> "A")
            .collect(Collectors.joining());
        Message message = new Message(longText, "33312333213", "test", "test", MessageType.FLASH);
        Address sourceAddress = DEFAULT_ADDRESS_BUILDER.createSourceAddress(message.getSource());
        Address destinationAddress = DEFAULT_ADDRESS_BUILDER.createDestinationAddress(message.getMsisdn());

        byte[] messageByte = CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_UCS_2);
        Tlv tlv = new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, messageByte);

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_DEFAULT);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setDataCoding((byte) 0x18);
        sm.setShortMessage(new byte[0]);
        sm.addOptionalParameter(tlv);

        return Arguments.of(message, false, sm);
    }

    @BeforeEach
    void setUp() {
        addressBuilder = mock(AddressBuilder.class);
        messageBuilder = new MessageBuilder(addressBuilder, new SubmitSmEncoderFactory());
    }

    @Test
    void shouldCreateCancelSm() {
        String messageId = "messageId";
        String source = "source";
        String msisdn = "msisdn";

        Address sourceAddress = new Address((byte) Ton.ALPHANUMERIC.toInt(), (byte) Npi.ISDN.toInt(), source);
        Address destinationAddress = new Address((byte) Ton.ALPHANUMERIC.toInt(), (byte) Npi.ISDN.toInt(), msisdn);

        CancelMessage cancelMessage = new CancelMessage(messageId, source, msisdn);

        when(addressBuilder.createSourceAddress(source)).thenReturn(sourceAddress);
        when(addressBuilder.createDestinationAddress(msisdn)).thenReturn(destinationAddress);

        CancelSm cancelSm = messageBuilder.createCancelSm(cancelMessage);

        assertEquals(messageId, cancelSm.getMessageId());
        assertEquals(sourceAddress, cancelSm.getSourceAddress());
        assertEquals(destinationAddress, cancelSm.getDestAddress());
    }

    @ParameterizedTest
    @MethodSource("simpleSubmitSmProvider")
    void shouldCreateExpectedSm(Message message, boolean ucs2Only, SubmitSm expected) {
        MessageBuilder messageBuilder = new MessageBuilder(DEFAULT_ADDRESS_BUILDER, new SubmitSmEncoderFactory());
        SubmitSm submitSm = messageBuilder.createSubmitSm(message, ucs2Only);

        assertEquals(expected.getEsmClass(), submitSm.getEsmClass());
        assertEquals(expected.getDataCoding(), submitSm.getDataCoding());
        assertEquals(expected.getOptionalParameterCount(), submitSm.getOptionalParameterCount());
        assertEquals(expected.getOptionalParameters(), submitSm.getOptionalParameters());
        assertArrayEquals(expected.getShortMessage(), submitSm.getShortMessage());
        assertEquals(expected.getRegisteredDelivery(), submitSm.getRegisteredDelivery());

        assertThat(submitSm.getSourceAddress()).isEqualToComparingFieldByField(expected.getSourceAddress());
        assertThat(submitSm.getDestAddress()).isEqualToComparingFieldByField(expected.getDestAddress());
        assertEquals(expected.getOptionalParameters(), submitSm.getOptionalParameters());
    }
}
