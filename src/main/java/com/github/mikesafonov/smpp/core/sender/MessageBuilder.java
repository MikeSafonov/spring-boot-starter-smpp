package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.CancelSm;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageType;
import com.github.mikesafonov.smpp.core.exceptions.IllegalAddressException;
import com.github.mikesafonov.smpp.core.exceptions.SmppMessageBuildingException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;


/**
 * @author MikeSafonov
 */
@Slf4j
public class MessageBuilder {

    private final AddressBuilder addressBuilder;
    private final SubmitSmEncoderFactory encoderFactory;

    public MessageBuilder(@NotNull TypeOfAddressParser typeOfAddressParser) {
        this(new AddressBuilder(typeOfAddressParser), new SubmitSmEncoderFactory());
    }

    public MessageBuilder(@NotNull AddressBuilder addressBuilder, @NotNull SubmitSmEncoderFactory encoderFactory) {
        this.addressBuilder = addressBuilder;
        this.encoderFactory = encoderFactory;
    }

    /**
     * Builds {@link SubmitSm} for sending via smpp.
     *
     * @param message  client message
     * @param ucs2Only use UCS2 encoding only or not
     * @return message {@link SubmitSm}.
     */
    @NotNull
    public SubmitSm createSubmitSm(@NotNull Message message, boolean ucs2Only) {
        try {
            Address sourceAddress = addressBuilder.createSourceAddress(message.getSource());
            Address destAddress = addressBuilder.createDestinationAddress(message.getMsisdn());

            return createSubmitSm(message, sourceAddress,
                    destAddress, ucs2Only);
        } catch (Exception e) {
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
    public CancelSm createCancelSm(CancelMessage cancelMessage) {
        Address sourceAddress = addressBuilder.createSourceAddress(cancelMessage.getSource());
        Address destAddress = addressBuilder.createDestinationAddress(cancelMessage.getMsisdn());

        CancelSm cancelSm = new CancelSm();
        cancelSm.setSourceAddress(sourceAddress);
        cancelSm.setDestAddress(destAddress);
        cancelSm.setMessageId(cancelMessage.getMessageId());
        return cancelSm;
    }

    private byte getEsmClass(MessageType messageType) {
        if(messageType == MessageType.DATAGRAM){
            return SmppConstants.ESM_CLASS_MM_DATAGRAM;
        }
        if(messageType == MessageType.FLASH){
            return SmppConstants.ESM_CLASS_MM_DEFAULT;
        }
        return SmppConstants.ESM_CLASS_MM_STORE_FORWARD;
    }


    /**
     * Builds {@link SubmitSm} for sending via smpp.
     *
     * @param message       client message
     * @param sourceAddress source address
     * @param destAddress   destination address
     * @return message {@link SubmitSm}.
     */
    @NotNull
    private SubmitSm createSubmitSm(@NotNull Message message, @NotNull Address sourceAddress, @NotNull Address destAddress,
                                    boolean ucs2Only) {

        byte esmClass = getEsmClass(message.getMessageType());
        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(esmClass);
        sm.setSourceAddress(sourceAddress);
        sm.setDestAddress(destAddress);
        encoderFactory.get(message).encode(message, sm, ucs2Only);

        if (message.getMessageType() == MessageType.SIMPLE) {
            sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        }
        return sm;
    }
}
