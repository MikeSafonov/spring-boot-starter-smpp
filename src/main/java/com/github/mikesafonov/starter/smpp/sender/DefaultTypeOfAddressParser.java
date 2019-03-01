package com.github.mikesafonov.starter.smpp.sender;

import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.github.mikesafonov.starter.smpp.sender.exceptions.IllegalAddressException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * Default implementation of {@link TypeOfAddressParser} interface. Supports international and alphanumeric
 * ton parameters, otherwise return {@link Ton#UNKNOWN}
 *
 * @author Mike Safonov
 */
@Slf4j
public class DefaultTypeOfAddressParser implements TypeOfAddressParser {

    private static final Pattern ALPHABET_PATTERN = Pattern.compile("^[a-zA-Z0-9_ -]{3,11}$");
    private static final Pattern INTERNATIONAL_PATTERN = Pattern.compile("^[0-9]{10,15}$");


    /**
     * Create TON and NPI parameters for message source {@code source}. Supported two types of source:
     * international or alphanumeric, otherwise return {@link Ton#UNKNOWN}
     *
     * @param source message source
     * @return ton and npi for source
     * @throws IllegalAddressException if exception occurs
     */
    @Override
    @NotNull
    public TypeOfAddress getSource(@NotNull String source) throws IllegalAddressException {
        try {
            if (checkPattern(source, INTERNATIONAL_PATTERN)) {
                return new TypeOfAddress(Ton.INTERNATIONAL, Npi.ISDN);
            } else if (checkPattern(source, ALPHABET_PATTERN)) {
                return new TypeOfAddress(Ton.ALPHANUMERIC, Npi.UNKNOWN);
            }
            return new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new IllegalAddressException(format("Source %s not supported", source));
        }
    }

    /**
     * Create TON and NPI parameters for message destination {@code destination}. If number in international number format
     * returns {@link Ton#INTERNATIONAL}, otherwise return {@link Ton#UNKNOWN}
     *
     * @param destination message destination
     * @return ton and npi for destination
     * @throws IllegalAddressException if exception occurs
     */
    @Override
    @NotNull
    public TypeOfAddress getDestination(@NotNull String destination) {
        try {
            if (checkPattern(destination, INTERNATIONAL_PATTERN)) {
                return new TypeOfAddress(Ton.INTERNATIONAL, Npi.ISDN);
            }
            return new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new IllegalAddressException(format("Destination %s not supported", destination));
        }
    }


    /**
     * Check is source matching specific pattern
     *
     * @param source text.
     * @return true if source is matching pattern
     */
    private boolean checkPattern(@NotNull String source, @NotNull Pattern pattern){
        return pattern.matcher(source).matches();
    }

}
