package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.commons.gsm.TypeOfAddress;

import javax.validation.constraints.NotNull;

/**
 * This implementation always returns {@link Ton#UNKNOWN} and {@link Npi#UNKNOWN}
 *
 * @author Mike Safonov
 */
public class UnknownTypeOfAddressParser implements TypeOfAddressParser {
    @Override
    public @NotNull TypeOfAddress getSource(@NotNull String source) {
        return new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);
    }

    @Override
    public @NotNull TypeOfAddress getDestination(@NotNull String destination) {
        return new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);
    }
}
