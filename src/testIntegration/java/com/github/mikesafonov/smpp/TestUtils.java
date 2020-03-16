package com.github.mikesafonov.smpp;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.connection.ConnectionManagerFactory;
import com.github.mikesafonov.smpp.core.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    static SenderClient createDefaultSenderClient(String name, SmppProperties.Credentials credentials) {
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(credentials);
        smsc.setMaxTry(5);
        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        ConnectionManager manager = new ConnectionManagerFactory().transmitter(name, defaults, smsc);
        return new ClientFactory().standardSender(name, defaults, smsc, new DefaultTypeOfAddressParser(), manager);
    }

}
