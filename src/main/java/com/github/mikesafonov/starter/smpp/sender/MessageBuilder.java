package com.github.mikesafonov.starter.smpp.sender;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageType;
import com.github.mikesafonov.starter.smpp.sender.exceptions.IllegalAddressException;
import com.github.mikesafonov.starter.smpp.utils.CountWithEncoding;
import com.github.mikesafonov.starter.smpp.utils.MessageUtil;
import lombok.RequiredArgsConstructor;


/**
 * @author MikeSafonov
 */
@RequiredArgsConstructor
public class MessageBuilder {

    private static final byte SILENT_CODING = (byte) 0xC0;

    private final AddressBuilder addressBuilder;

    public SubmitSm createSubmitSm(Message message, MessageType messageType, boolean ucs2Only) throws SmppInvalidArgumentException, IllegalAddressException {
        return createSubmitSm(message, messageType, false, ucs2Only);
    }

    public SubmitSm createSubmitSm(Message message, MessageType messageType, boolean silent, boolean ucs2Only) throws SmppInvalidArgumentException, IllegalAddressException {
        byte esmClass = getEsmClass(messageType);
        Address sourceAddress = addressBuilder.createSourceAddress(message.getSource());
        Address destAddress = addressBuilder.createDestAddress(message.getMsisdn());

        SubmitSm submitSm = createSubmitSm(message.getText(), esmClass, sourceAddress, destAddress, silent, ucs2Only);

        if (messageType != MessageType.DATAGRAM) {
            registerDeliveryReport(submitSm);
        }

        return submitSm;
    }

    private byte getEsmClass(MessageType messageType) {
        return (messageType == MessageType.DATAGRAM) ? SmppConstants.ESM_CLASS_MM_DATAGRAM : SmppConstants.ESM_CLASS_MM_STORE_FORWARD;
    }


    /**
     * Метод создает сообщение для отправки СМСЦ.
     *
     * @param message       сообщение пришедшее от клиента.
     * @param esmClass      ESM_CLASS, см.
     *                      {@link SmppConstants#ESM_CLASS_MM_STORE_FORWARD} и
     *                      {@link SmppConstants#ESM_CLASS_MM_DATAGRAM}
     * @param sourceAddress адресс отправителя
     * @param destAddress   адресс получателя
     * @return сообщение {@link SubmitSm}.
     * @throws SmppInvalidArgumentException в случае ошибок при конвертации текста сообщения в байт
     */

    private SubmitSm createSubmitSm(String message, byte esmClass, Address sourceAddress, Address destAddress, boolean silent, boolean ucs2Only) throws SmppInvalidArgumentException {

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(esmClass);
        sm.setSourceAddress(sourceAddress);
        sm.setDestAddress(destAddress);
        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(message, ucs2Only);
        byte coding = findCoding(countWithEncoding, silent);
        sm.setDataCoding(coding);
        byte[] messageByte = CharsetUtil.encode(message, countWithEncoding.getCharset());
        if (countWithEncoding.getCount() > 1) {
            sm.setShortMessage(new byte[0]);
            sm.addOptionalParameter(new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, messageByte)); //0x0424 is an optional parameter code for payload
        } else {
            sm.setShortMessage(messageByte);
        }
        return sm;
    }

    private byte findCoding(CountWithEncoding countWithEncoding, boolean silent) {
        if (silent) {
            return SILENT_CODING;
        }

        if (countWithEncoding.getCharset() == CharsetUtil.CHARSET_GSM) {
            return SmppConstants.DATA_CODING_DEFAULT;
        } else {
            return SmppConstants.DATA_CODING_UCS2;
        }
    }

    private void registerDeliveryReport(SubmitSm sm) {
        sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
    }
}
