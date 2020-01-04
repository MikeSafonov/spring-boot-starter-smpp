package com.github.mikesafonov.smpp;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.ServerSocket;

@UtilityClass
public class TestUtils {
    static SenderClient createDefaultSenderClient(String name, SmppProperties.Credentials credentials) {
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(credentials);
        smsc.setMaxTry(5);
        return new ClientFactory().standardSender(name, new SmppProperties.Defaults(),
                smsc, new DefaultTypeOfAddressParser());
    }

    static SmppProperties.Credentials credentials() {
        SmppProperties.Credentials credentials = new SmppProperties.Credentials();
        credentials.setPort(findRandomOpenPortOnAllLocalInterfaces());
        credentials.setHost("localhost");
        credentials.setUsername("username");
        credentials.setPassword("password");
        return credentials;
    }

    static Integer findRandomOpenPortOnAllLocalInterfaces() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to find port", e);
        }
    }
}
