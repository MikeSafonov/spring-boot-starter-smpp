package com.github.mikesafonov.starter.smpp.sender;

import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.cloudhopper.smpp.type.Address;
import com.github.mikesafonov.starter.smpp.sender.exceptions.IllegalAddressException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;


/**
 * @author MikeSafonov
 */
@RequiredArgsConstructor
public class AddressBuilder {

    private final TypeOfAddressParser addressParser;


    /**
     * Метод создает адрес отправителя сообщения. Сначала производится анализ
     * источника сообщения с помощью {@link DefaultTypeOfAddressParser#getSourceParameters(String)}. В
     * случае ошибки используются параметры по умолчанию.
     *
     * @param source message source
     * @return source address
     * @throws IllegalAddressException if source not supported
     */
    @NotNull
    public Address createSourceAddress(@Nullable String source) throws IllegalAddressException {
        TypeOfAddress typeOfAddress = addressParser.getSource(source);
        byte ton = (byte) typeOfAddress.getTon().toInt();
        byte npi = (byte) typeOfAddress.getNpi().toInt();
        return new Address(ton, npi, source);
    }

    /**
     * Метод создает адрес получателя сообщения. Предполагается, что номер абонента находится в
     * международном формате. Однако, во избежание ситуаций когда номер будет не в международном формате,
     * мы добавили сюда проверку формата с помощью PhoneNormalizer.
     *
     * @param msisdn destination phone number
     * @return destination address
     */
    @NotNull
    public Address createDestAddress(@Nullable String msisdn) {
        TypeOfAddress typeOfAddress = addressParser.getDestination(msisdn);
        byte ton = (byte) typeOfAddress.getTon().toInt();
        byte npi = (byte) typeOfAddress.getNpi().toInt();
        return new Address(ton, npi, msisdn);
    }
}
