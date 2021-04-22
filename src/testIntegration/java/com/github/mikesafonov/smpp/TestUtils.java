package com.github.mikesafonov.smpp;

import com.cloudhopper.smpp.SmppServerSession;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.connection.ConnectionManagerFactory;
import com.github.mikesafonov.smpp.core.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.assertj.core.api.IterableAssert;

import java.util.Set;

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

    public static InstanceOfAssertFactory<Set, IterableAssert<SmppServerSession>> sessionSet() {
        return new InstanceOfAssertFactory<>(Set.class, Assertions::<SmppServerSession>assertThat);
    }

}
