package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.github.mikesafonov.smpp.core.exceptions.IllegalAddressException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mike Safonov
 */
class DefaultTypeOfAddressParserTest {

    private static final TypeOfAddress unknown = new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);

    private static List<String> invalidSourceDestinationProvider() {
        return asList("3805030922353333", "",  "$$$$$$", "@fasd", "Прив");
    }

    private static List<String> numericSourceDestinationProvider() {
        return asList("380223092235", "780223092235");
    }

    private static List<String> textSourceDestinationProvider() {
        return asList("TEST-A", "Test A", "Test_a", "Hello");
    }

    private DefaultTypeOfAddressParser defaultTypeOfAddressParser;

    @BeforeEach
    void setUp() {
        defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
    }

    @ParameterizedTest
    @MethodSource("invalidSourceDestinationProvider")
    void shouldReturnUnknownBecauseSourceInvalid(String invalidSource) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getSource(invalidSource);

        assertEquals(unknown.getTon(), typeOfAddress.getTon());
        assertEquals(unknown.getNpi(), typeOfAddress.getNpi());
    }

    @ParameterizedTest
    @MethodSource("numericSourceDestinationProvider")
    void shouldReturnNumericSource(String numericSource) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getSource(numericSource);

        assertEquals(Ton.INTERNATIONAL, typeOfAddress.getTon());
        assertEquals(Npi.ISDN, typeOfAddress.getNpi());
    }

    @ParameterizedTest
    @MethodSource("textSourceDestinationProvider")
    void shouldReturnTextSource(String numericSource) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getSource(numericSource);

        assertEquals(Ton.ALPHANUMERIC, typeOfAddress.getTon());
        assertEquals(Npi.UNKNOWN, typeOfAddress.getNpi());
    }

    @Test
    void shouldThrowIllegalSourceAddressException(){
        assertThrows(IllegalAddressException.class, () -> defaultTypeOfAddressParser.getSource(null));
    }

    @ParameterizedTest
    @MethodSource("numericSourceDestinationProvider")
    void shouldReturnNumericDestination(String numericDestination) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getDestination(numericDestination);

        assertEquals(Ton.INTERNATIONAL, typeOfAddress.getTon());
        assertEquals(Npi.ISDN, typeOfAddress.getNpi());
    }

    @ParameterizedTest
    @MethodSource("textSourceDestinationProvider")
    void shouldReturnUnknownDestination(String destination) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getDestination(destination);

        assertEquals(Ton.UNKNOWN, typeOfAddress.getTon());
        assertEquals(Npi.UNKNOWN, typeOfAddress.getNpi());
    }

    @Test
    void shouldThrowIllegalDestinationAddressException(){
        assertThrows(IllegalAddressException.class, () -> defaultTypeOfAddressParser.getDestination(null));
    }

}
