package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.gsm.Npi;
import com.cloudhopper.commons.gsm.Ton;
import com.cloudhopper.commons.gsm.TypeOfAddress;
import com.cloudhopper.smpp.type.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class AddressBuilderTest {

    private AddressBuilder addressBuilder;
    private TypeOfAddressParser addressParser;

    @BeforeEach
    void setUp() {
        addressParser = mock(TypeOfAddressParser.class);
        addressBuilder = new AddressBuilder(addressParser);

    }

    @Test
    void shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddressBuilder(null));
    }

    @Test
    void shouldThrow() {
        when(addressParser.getSource(anyString())).thenThrow(RuntimeException.class);
        when(addressParser.getDestination(anyString())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> addressBuilder.createSourceAddress("string"));
        assertThrows(RuntimeException.class, () -> addressBuilder.createDestinationAddress("string"));
    }

    @Test
    void shouldReturnExceptedSourceAddress(){
        TypeOfAddress typeOfAddress = new TypeOfAddress(Ton.INTERNATIONAL, Npi.ISDN);
        when(addressParser.getSource(anyString())).thenReturn(typeOfAddress);

        String value = "asdads";
        Address sourceAddress = addressBuilder.createSourceAddress(value);

        assertEquals(value, sourceAddress.getAddress());
        assertEquals((byte)typeOfAddress.getTon().toInt(), sourceAddress.getTon());
        assertEquals((byte)typeOfAddress.getNpi().toInt(), sourceAddress.getNpi());
    }

    @Test
    void shouldReturnExceptedDestinationAddress(){
        TypeOfAddress typeOfAddress = new TypeOfAddress(Ton.INTERNATIONAL, Npi.ISDN);
        when(addressParser.getDestination(anyString())).thenReturn(typeOfAddress);

        String value = "asdads";
        Address destinationAddress = addressBuilder.createDestinationAddress(value);

        assertEquals(value, destinationAddress.getAddress());
        assertEquals((byte)typeOfAddress.getTon().toInt(), destinationAddress.getTon());
        assertEquals((byte)typeOfAddress.getNpi().toInt(), destinationAddress.getNpi());
    }



}
