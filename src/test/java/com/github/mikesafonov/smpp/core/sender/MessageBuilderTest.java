package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.CancelSm;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class MessageBuilderTest {

    private AddressBuilder addressBuilder;
    private MessageBuilder messageBuilder;

    private static Stream<Arguments> simpleSubmitSmProvider() throws SmppInvalidArgumentException {
        return Stream.of(
                simpleMessageWithLatinText()
        );
    }

    private static Arguments simpleMessageWithLatinText() throws SmppInvalidArgumentException {
        Message message = new Message("test", "test", "test", "test", MessageType.SIMPLE);
        Address sourceAddress = new Address((byte) Ton.ALPHANUMERIC.toInt(), (byte) Npi.ISDN.toInt(), message.getSource());
        Address destinationAddress = new Address((byte) Ton.ALPHANUMERIC.toInt(), (byte) Npi.ISDN.toInt(), message.getMsisdn());

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        sm.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);
        sm.setShortMessage(CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_GSM));

        return Arguments.of(message, false, sm);
    }

    private static Arguments simpleLargeMessageWithLatinText() throws SmppInvalidArgumentException {
        Message message = new Message("test", "test", "test", "test", MessageType.SIMPLE);
        Address sourceAddress = new Address((byte) Ton.ALPHANUMERIC.toInt(), (byte) Npi.ISDN.toInt(), message.getSource());
        Address destinationAddress = new Address((byte) Ton.ALPHANUMERIC.toInt(), (byte) Npi.ISDN.toInt(), message.getMsisdn());

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD);
        sm.setDestAddress(destinationAddress);
        sm.setSourceAddress(sourceAddress);
        sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        sm.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);
        sm.setShortMessage(CharsetUtil.encode(message.getText(), CharsetUtil.CHARSET_GSM));

        return Arguments.of(message, false, sm);
    }

    @BeforeEach
    void setUp() {
        addressBuilder = mock(AddressBuilder.class);
        messageBuilder = new MessageBuilder(addressBuilder);
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

//    @ParameterizedTest
//    @MethodSource("simpleSubmitSmProvider")
//    void shouldCreateExpectedSm(Message message, boolean ucs2Only, SubmitSm expected) throws SmppInvalidArgumentException {
//        SubmitSm submitSm = messageBuilder.createSubmitSm(message, ucs2Only);
//
//        assertEquals(expected.getEsmClass(), submitSm.getEsmClass());
//        assertEquals(expected.getSourceAddress(), submitSm.getSourceAddress());
//        assertEquals(expected.getDestAddress(), submitSm.getDestAddress());
//        assertEquals(expected.getDataCoding(), submitSm.getDataCoding());
//        assertEquals(expected.getOptionalParameterCount(), submitSm.getOptionalParameterCount());
//        assertEquals(expected.getOptionalParameters(), submitSm.getOptionalParameters());
//        assertArrayEquals(expected.getShortMessage(), submitSm.getShortMessage());
//        assertEquals(expected.getRegisteredDelivery(), submitSm.getRegisteredDelivery());
//    }
}
