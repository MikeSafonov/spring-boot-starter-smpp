package com.github.mikesafonov.starter.smpp.sender;

import com.cloudhopper.commons.charset.Charset;
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

import javax.validation.constraints.NotNull;


/**
 * @author MikeSafonov
 */
@RequiredArgsConstructor
public class MessageBuilder {

    private static final byte SILENT_CODING = (byte) 0xC0;

    private final AddressBuilder addressBuilder;

    /**
     * Builds {@link SubmitSm} for sending via smpp.
     *
     * @param message  client message
     * @param silent   is message must be silent
     * @param ucs2Only use UCS2 encoding only or not
     * @return message {@link SubmitSm}.
     * @throws SmppInvalidArgumentException see {@link SubmitSm#setShortMessage}
     */
    @NotNull
    public SubmitSm createSubmitSm(@NotNull Message message, boolean silent, boolean ucs2Only) throws SmppInvalidArgumentException, IllegalAddressException {
        byte esmClass = getEsmClass(message.getMessageType());
        Address sourceAddress = addressBuilder.createSourceAddress(message.getSource());
        Address destAddress = addressBuilder.createDestAddress(message.getMsisdn());

        SubmitSm submitSm = createSubmitSm(message.getText(), esmClass, sourceAddress, destAddress, silent, ucs2Only);

        if (!message.isDatagram()) {
            registerDeliveryReport(submitSm);
        }

        return submitSm;
    }

    private byte getEsmClass(MessageType messageType) {
        return (messageType == MessageType.DATAGRAM) ? SmppConstants.ESM_CLASS_MM_DATAGRAM : SmppConstants.ESM_CLASS_MM_STORE_FORWARD;
    }


    /**
     * Builds {@link SubmitSm} for sending via smpp.
     *
     * @param message       client message
     * @param esmClass      ESM_CLASS, see
     *                      {@link SmppConstants#ESM_CLASS_MM_STORE_FORWARD} /
     *                      {@link SmppConstants#ESM_CLASS_MM_DATAGRAM}
     * @param sourceAddress source address
     * @param destAddress   destination address
     * @return message {@link SubmitSm}.
     * @throws SmppInvalidArgumentException see {@link SubmitSm#setShortMessage}
     */
    @NotNull
    private SubmitSm createSubmitSm(@NotNull String message, byte esmClass, @NotNull Address sourceAddress, @NotNull Address destAddress, boolean silent, boolean ucs2Only) throws SmppInvalidArgumentException {

        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(esmClass);
        sm.setSourceAddress(sourceAddress);
        sm.setDestAddress(destAddress);
        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(message, ucs2Only);
        byte coding = findCoding(countWithEncoding.getCharset(), silent);
        sm.setDataCoding(coding);
        byte[] messageByte = CharsetUtil.encode(message, countWithEncoding.getCharset());
        if (countWithEncoding.getCount() > 1) {
            sm.setShortMessage(new byte[0]);
            sm.addOptionalParameter(new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, messageByte));
        } else {
            sm.setShortMessage(messageByte);
        }
        return sm;
    }

    private byte findCoding(@NotNull Charset charset, boolean silent) {
        if (silent) {
            return SILENT_CODING;
        }

        return (charset == CharsetUtil.CHARSET_GSM) ? SmppConstants.DATA_CODING_DEFAULT :
                SmppConstants.DATA_CODING_UCS2;
    }

    private void registerDeliveryReport(@NotNull SubmitSm sm) {
        sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
    }
}
