package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.clients.MockSenderClient;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.clients.TestSenderClient;
import com.github.mikesafonov.starter.smpp.config.ReceiverConfiguration;
import com.github.mikesafonov.starter.smpp.config.TransmitterConfiguration;
import com.github.mikesafonov.starter.smpp.reciever.DefaultResponseClient;
import com.github.mikesafonov.starter.smpp.reciever.ResponseClient;
import com.github.mikesafonov.starter.smpp.sender.DefaultSenderClient;
import com.github.mikesafonov.starter.smpp.sender.MessageBuilder;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

@UtilityClass
public class ClientFactory {

    public static SenderClient mockSender(@NotBlank String name, @NotNull SmppResultGenerator smppResultGenerator) {
        return new MockSenderClient(smppResultGenerator, name);
    }

    public static SenderClient testSender(@NotBlank String name, @NotNull SmppResultGenerator smppResultGenerator, @NotNull SmppProperties.SMSC smsc, @NotNull MessageBuilder messageBuilder) {
        TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(smsc);
        SenderClient senderClient = DefaultSenderClient.of(transmitterConfiguration, smsc.getMaxTry(),
                smsc.isUcs2Only(), smsc.getRequestTimeout().toMillis(), messageBuilder, name);
        return new TestSenderClient(senderClient, Arrays.asList(smsc.getAllowedPhones()), smppResultGenerator);
    }

    public static SenderClient defaultSender(@NotBlank String name, @NotNull SmppProperties.SMSC smsc, @NotNull MessageBuilder messageBuilder) {
        TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(smsc);
        return DefaultSenderClient.of(transmitterConfiguration, smsc.getMaxTry(),
                smsc.isUcs2Only(), smsc.getRequestTimeout().toMillis(), messageBuilder, name);
    }

    public static ResponseClient defaultResponse(@NotBlank String name, @NotNull SmppProperties.SMSC smsc) {
        ReceiverConfiguration receiverConfiguration = new ReceiverConfiguration(smsc);
        return DefaultResponseClient.of(receiverConfiguration, smsc.getRebindPeriod().getSeconds(), name);
    }
}
