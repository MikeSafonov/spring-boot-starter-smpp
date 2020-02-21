package com.github.mikesafonov.smpp;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    static SenderClient createDefaultSenderClient(String name, SmppProperties.Credentials credentials) {
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(credentials);
        smsc.setMaxTry(5);
        return new ClientFactory().standardSender(name, new SmppProperties.Defaults(),
                smsc, new DefaultTypeOfAddressParser());
    }

}
