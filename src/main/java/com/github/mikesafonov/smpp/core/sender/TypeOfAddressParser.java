package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.gsm.TypeOfAddress;

import javax.validation.constraints.NotNull;

/**
 * Interface represents parser of TON and NPI parameters for source and destination address of message.
 *
 * @author Mike Safonov
 */
public interface TypeOfAddressParser {

    /**
     * Detect ton and npi parameters for message source address
     *
     * @param source message source
     * @return ton and npi for message source
     */
    @NotNull TypeOfAddress getSource(@NotNull String source);

    /**
     * Detect ton and npi parameters for message destination address
     *
     * @param destination message destination
     * @return ton and npi for message destination
     */
    @NotNull TypeOfAddress getDestination(@NotNull String destination);

}
