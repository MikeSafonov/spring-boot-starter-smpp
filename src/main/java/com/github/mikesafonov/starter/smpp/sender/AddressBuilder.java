package com.github.mikesafonov.starter.smpp.sender;

import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.cloudhopper.smpp.type.Address;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;


/**
 * Class for building {@link Address} of source and destination
 *
 * @author Mike Safonov
 */
public class AddressBuilder {

    private final TypeOfAddressParser addressParser;

    public AddressBuilder(@NotNull TypeOfAddressParser addressParser) {
        this.addressParser = requireNonNull(addressParser);
    }

    /**
     * Detect TON and NPI parameters from {@code source} and convert to {@link Address}
     *
     * @param source message source
     * @return source address
     */
    @NotNull
    public Address createSourceAddress(@NotNull String source) {
        return convertToAddress(source, addressParser.getSource(source));
    }


    /**
     * Detect TON and NPI parameters from {@code msisdn} and convert to {@link Address}
     *
     * @param msisdn destination phone number
     * @return destination address
     */
    @NotNull
    public Address createDestAddress(@NotNull String msisdn) {
        return convertToAddress(msisdn, addressParser.getDestination(msisdn));
    }

    private Address convertToAddress(String value, TypeOfAddress typeOfAddress) {
        byte ton = (byte) typeOfAddress.getTon().toInt();
        byte npi = (byte) typeOfAddress.getNpi().toInt();
        return new Address(ton, npi, value);
    }
}
