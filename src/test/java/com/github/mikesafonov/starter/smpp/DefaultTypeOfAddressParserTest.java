package com.github.mikesafonov.starter.smpp;

import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.github.mikesafonov.starter.smpp.sender.DefaultTypeOfAddressParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mike Safonov
 */
class DefaultTypeOfAddressParserTest {

    private static final TypeOfAddress unknown = new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);

    private static List<String> invalidSource() {
        return asList("3805030922353333", "",  "$$$$$$", "@fasd", "Прив");
    }

    private static List<String> numericSource() {
        return asList("380223092235", "780223092235");
    }

    private static List<String> textSource() {
        return asList("TEST-A", "Test A", "Test_a", "Hello");
    }

    private DefaultTypeOfAddressParser defaultTypeOfAddressParser;

    @BeforeEach
    void setUp() {
        defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
    }

    @ParameterizedTest
    @MethodSource("invalidSource")
    void shouldReturnUnknownBecauseSourceInvalid(String invalidSource) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getSource(invalidSource);

        assertEquals(unknown.getTon(), typeOfAddress.getTon());
        assertEquals(unknown.getNpi(), typeOfAddress.getNpi());
    }

    @ParameterizedTest
    @MethodSource("numericSource")
    void shouldReturnNumericSource(String numericSource) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getSource(numericSource);
        assertEquals(Ton.INTERNATIONAL, typeOfAddress.getTon());
        assertEquals(Npi.ISDN, typeOfAddress.getNpi());
    }

    @ParameterizedTest
    @MethodSource("textSource")
    void shouldReturnTextSource(String numericSource) {
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getSource(numericSource);

        assertEquals(Ton.ALPHANUMERIC, typeOfAddress.getTon());
        assertEquals(Npi.UNKNOWN, typeOfAddress.getNpi());
    }
}
