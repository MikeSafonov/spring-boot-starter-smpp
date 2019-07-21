package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.CancelSm;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageType;
import com.github.mikesafonov.smpp.core.sender.exceptions.IllegalAddressException;
import com.github.mikesafonov.smpp.core.sender.exceptions.SmppMessageBuildingException;
import com.github.mikesafonov.smpp.core.utils.CountWithEncoding;
import com.github.mikesafonov.smpp.core.utils.MessageUtil;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;


/**
 * @author MikeSafonov
 */
@Slf4j
public class MessageBuilder {

    private static final byte SILENT_CODING = (byte) 0xC0;

    private final AddressBuilder addressBuilder;

    public MessageBuilder(@NotNull TypeOfAddressParser typeOfAddressParser) {
       this(new AddressBuilder(typeOfAddressParser));
    }

    public MessageBuilder(@NotNull AddressBuilder addressBuilder) {
        this.addressBuilder = addressBuilder;
    }

    /**
     * Builds {@link SubmitSm} for sending via smpp.
     *
     * @param message  client message
     * @param ucs2Only use UCS2 encoding only or not
     * @return message {@link SubmitSm}.
     */
    @NotNull
    public SubmitSm createSubmitSm(@NotNull Message message,  boolean ucs2Only) {
        try {
            byte esmClass = getEsmClass(message.getMessageType());
            Address sourceAddress = addressBuilder.createSourceAddress(message.getSource());
            Address destAddress = addressBuilder.createDestinationAddress(message.getMsisdn());

            SubmitSm submitSm = createSubmitSm(message.getText(), esmClass, sourceAddress, destAddress, message.isSilent(), ucs2Only);

            if (message.getMessageType() == MessageType.SIMPLE) {
                registerDeliveryReport(submitSm);
            }

            return submitSm;
        } catch (SmppInvalidArgumentException e) {
            log.error(e.getMessage(), e);
            throw new SmppMessageBuildingException();
        }
    }

    /**
     * Builds {@link CancelSm} for canceling sms message
     *
     * @param cancelMessage cancel message
     * @return request {@link CancelSm}
     * @throws IllegalAddressException if source/destination address not created
     */
    public CancelSm createCancelSm(CancelMessage cancelMessage){
        Address sourceAddress = addressBuilder.createSourceAddress(cancelMessage.getSource());
        Address destAddress = addressBuilder.createDestinationAddress(cancelMessage.getMsisdn());

        CancelSm cancelSm = new CancelSm();
        cancelSm.setSourceAddress(sourceAddress);
        cancelSm.setDestAddress(destAddress);
        cancelSm.setMessageId(cancelMessage.getMessageId());
        return cancelSm;
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
