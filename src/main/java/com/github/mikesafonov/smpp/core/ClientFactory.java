package com.github.mikesafonov.smpp.core;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.reciever.ResponseClient;
import com.github.mikesafonov.smpp.core.reciever.StandardResponseClient;
import com.github.mikesafonov.smpp.core.sender.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.mikesafonov.smpp.core.utils.Utils.getOrDefault;
import static com.github.mikesafonov.smpp.core.utils.Utils.validateName;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

/**
 * Helper class for building {@link SenderClient} and {@link ResponseClient}
 */
public class ClientFactory {

    /**
     * Creates {@link MockSenderClient} with name {@code name} and {@link SmppResultGenerator}
     * {@code smppResultGenerator}
     *
     * @param name                name of client
     * @param smppResultGenerator result generator
     * @return mock sender client
     */
    public SenderClient mockSender(@NotBlank String name, @NotNull SmppResultGenerator smppResultGenerator) {
        validateName(name);
        requireNonNull(smppResultGenerator);

        return new MockSenderClient(smppResultGenerator, name);
    }

    /**
     * Creates {@link TestSenderClient} with base client {@code senderClient}, {@link SmppResultGenerator}
     * {@code smppResultGenerator} and list of allowed phones from {@code smsc} or {@code defaults}
     *
     * @param name                name of client
     * @param defaults            default smpp properties
     * @param smsc                smpp properties
     * @param typeOfAddressParser address parser
     * @param connectionManager   connection manager
     * @param smppResultGenerator result generator for not allowed phones
     * @return test sender client
     */
    public SenderClient testSender(@NotBlank String name,
                                   @NotNull SmppProperties.Defaults defaults,
                                   @NotNull SmppProperties.SMSC smsc,
                                   @NotNull TypeOfAddressParser typeOfAddressParser,
                                   @NotNull ConnectionManager connectionManager,
                                   @NotNull SmppResultGenerator smppResultGenerator) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);
        requireNonNull(typeOfAddressParser);
        requireNonNull(connectionManager);

        boolean ucs2Only = getOrDefault(smsc.getUcs2Only(), defaults.isUcs2Only());
        long requestTimeout = getOrDefault(smsc.getRequestTimeout(), defaults.getRequestTimeout()).toMillis();


        String[] phones = getOrDefault(smsc.getAllowedPhones(), defaults.getAllowedPhones());

        Set<String> allowedPhones = (phones == null) ? emptySet() : Arrays.stream(phones).collect(Collectors.toSet());
        return new TestSenderClient(connectionManager,
            ucs2Only, requestTimeout, new MessageBuilder(typeOfAddressParser), allowedPhones, smppResultGenerator);
    }

    /**
     * Creates {@link StandardSenderClient} with name {@code name} and {@link TypeOfAddressParser}
     * {@code typeOfAddressParser},
     * configured with properties from {@code smsc} or {@code defaults}
     *
     * @param name                name of client
     * @param defaults            default smpp properties
     * @param smsc                smpp properties
     * @param typeOfAddressParser address parser
     * @param connectionManager   connection manager
     * @return standard sender client
     */
    public SenderClient standardSender(@NotBlank String name,
                                       @NotNull SmppProperties.Defaults defaults,
                                       @NotNull SmppProperties.SMSC smsc,
                                       @NotNull TypeOfAddressParser typeOfAddressParser,
                                       @NotNull ConnectionManager connectionManager) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);
        requireNonNull(typeOfAddressParser);
        requireNonNull(connectionManager);

        boolean ucs2Only = getOrDefault(smsc.getUcs2Only(), defaults.isUcs2Only());
        long requestTimeout = getOrDefault(smsc.getRequestTimeout(), defaults.getRequestTimeout()).toMillis();

        return new StandardSenderClient(connectionManager,
                ucs2Only, requestTimeout, new MessageBuilder(typeOfAddressParser));
    }

    /**
     * Creates {@link StandardResponseClient} with name {@code name}, configured with properties from
     * {@code smsc} or {@code defaults}
     *
     * @param name     name of client
     * @param connectionManager     connection manager
     * @return standard response client
     */
    public ResponseClient standardResponse(@NotBlank String name, @NotNull ConnectionManager connectionManager) {
        validateName(name);
        requireNonNull(connectionManager);

        return new StandardResponseClient(connectionManager);
    }
}
