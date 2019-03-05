package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.clients.MockSenderClient;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.clients.TestSenderClient;
import com.github.mikesafonov.starter.smpp.reciever.DefaultResponseClient;
import com.github.mikesafonov.starter.smpp.reciever.ReceiverConfiguration;
import com.github.mikesafonov.starter.smpp.reciever.ResponseClient;
import com.github.mikesafonov.starter.smpp.sender.DefaultSenderClient;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import com.github.mikesafonov.starter.smpp.sender.TransmitterConfiguration;
import com.github.mikesafonov.starter.smpp.sender.TypeOfAddressParser;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ClientFactory {

    public static SenderClient mockSender(@NotBlank String name, @NotNull SmppResultGenerator smppResultGenerator) {
        return new MockSenderClient(smppResultGenerator, name);
    }

//    public static SenderClient testSender(@NotBlank String name, @NotNull SmppResultGenerator smppResultGenerator,
//                                          @NotNull SmppProperties.SMSC smsc, @NotNull TypeOfAddressParser typeOfAddressParser) {
//        TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(name, smsc);
//        SenderClient senderClient = DefaultSenderClient.of(transmitterConfiguration, smsc.getMaxTry(),
//                smsc.isUcs2Only(), smsc.getRequestTimeout().toMillis(), typeOfAddressParser);
//        return new TestSenderClient(senderClient, Arrays.asList(smsc.getAllowedPhones()), smppResultGenerator);
//    }

    public static SenderClient testSender(@NotNull SenderClient senderClient, @NotNull SmppProperties.Defaults defaults,
                                          @NotNull SmppResultGenerator smppResultGenerator,
                                          @NotNull SmppProperties.SMSC smsc) {
        List<String> allowedPhones = (smsc.getAllowedPhones() == null) ? Arrays.asList(defaults.getAllowedPhones()) : Arrays.asList(smsc.getAllowedPhones());
        return new TestSenderClient(senderClient, allowedPhones, smppResultGenerator);
    }

    public static SenderClient defaultSender(@NotBlank String name, @NotNull SmppProperties.Defaults defaults, @NotNull SmppProperties.SMSC smsc,
                                             @NotNull TypeOfAddressParser typeOfAddressParser) {
        boolean loggingBytes = (smsc.getLoggingBytes() == null) ? defaults.isLoggingBytes() : smsc.getLoggingBytes();
        boolean loggingPdu = (smsc.getLoggingPdu() == null) ? defaults.isLoggingPdu() : smsc.getLoggingPdu();
        int windowsSize = (smsc.getWindowSize() == null) ? defaults.getWindowSize() : smsc.getWindowSize();
        boolean ucs2Only = (smsc.getUcs2Only() == null) ? defaults.isUcs2Only() : smsc.getUcs2Only();
        long requestTimeout = (smsc.getRequestTimeout() == null) ? defaults.getRequestTimeout().toMillis() : smsc.getRequestTimeout().toMillis();
        TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(name, smsc.getCredentials(), loggingBytes, loggingPdu, windowsSize);
        return DefaultSenderClient.of(transmitterConfiguration, smsc.getMaxTry(),
                ucs2Only, requestTimeout, typeOfAddressParser);
    }

    public static ResponseClient defaultResponse(@NotBlank String name, @NotNull SmppProperties.Defaults defaults, @NotNull SmppProperties.SMSC smsc) {
        boolean loggingBytes = (smsc.getLoggingBytes() == null) ? defaults.isLoggingBytes() : smsc.getLoggingBytes();
        boolean loggingPdu = (smsc.getLoggingPdu() == null) ? defaults.isLoggingPdu() : smsc.getLoggingPdu();
        long rebindPeriod = (smsc.getRebindPeriod() == null) ? defaults.getRebindPeriod().getSeconds() : smsc.getRebindPeriod().getSeconds();
        ReceiverConfiguration receiverConfiguration = new ReceiverConfiguration(name, smsc.getCredentials(), loggingBytes, loggingPdu);
        return DefaultResponseClient.of(receiverConfiguration, rebindPeriod);
    }
}
