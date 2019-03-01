package com.github.mikesafonov.starter.smpp;

import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.cloudhopper.smpp.SmppConstants;
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

    private static List<String> invalidSource() {
        return asList("3805030922353333", "", null, "$$$$$$", "@fasd", "Прив");
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
    void shouldReturnNullBecauseSourceInvalid(String invalidSource) {
        TypeOfAddress unknown = new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);
        TypeOfAddress typeOfAddress = defaultTypeOfAddressParser.getSource(invalidSource);

        assertEquals(unknown.getTon(), typeOfAddress.getTon());
        assertEquals(unknown.getNpi(), typeOfAddress.getNpi());
    }

//
//    @ParameterizedTest
//    @MethodSource("numericSource")
//    void shouldReturnNumericSource(String numericSource) {
//        SourceParameters sourceParameters = defaultTypeOfAddressParser.getSourceParameters(numericSource).orElseThrow(NullPointerException::new);
//
//        assertEquals(SmppConstants.TON_INTERNATIONAL, sourceParameters.getSourceTon());
//        assertEquals(SmppConstants.NPI_E164, sourceParameters.getSourceNpi());
//    }
//
//    @ParameterizedTest
//    @MethodSource("textSource")
//    void shouldReturnTextSource(String numericSource) {
//        SourceParameters sourceParameters = defaultTypeOfAddressParser.getSourceParameters(numericSource).orElseThrow(NullPointerException::new);
//
//        assertEquals(SmppConstants.TON_ALPHANUMERIC, sourceParameters.getSourceTon());
//        assertEquals(SmppConstants.NPI_UNKNOWN, sourceParameters.getSourceNpi());
//    }
}
