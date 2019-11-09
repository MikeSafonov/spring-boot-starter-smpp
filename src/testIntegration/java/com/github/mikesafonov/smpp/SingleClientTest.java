package com.github.mikesafonov.smpp;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.CancelSm;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import com.github.mikesafonov.smpp.server.MockSmppServer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static com.github.mikesafonov.smpp.asserts.SmppAssertions.assertThat;

@Log4j2
public class SingleClientTest {
    private SmppProperties.Credentials credentials;
    private MockSmppServer mockSmppServer;
    private SenderClient client;

    @BeforeEach
    void runMockServer() throws SmppChannelException {
        credentials = credentials();
        mockSmppServer = new MockSmppServer(credentials);
        mockSmppServer.start();

        client = createDefaultSenderClient();
    }

    @AfterEach
    void stopMockServer() {
        mockSmppServer.stop();
    }

    @Test
    void shouldSendSingleSimpleMessage() {
        Message message = Message.simple("asdasd")
                .from("123123123")
                .to("12312312")
                .build();
        client.send(message);

        assertThat(mockSmppServer).hasSingleMessage()
                .hasEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD)
                .hasDest("12312312")
                .hasSource("123123123")
                .hasText("asdasd")
                .notSilent()
                .hasDeliveryReport();
    }

    @Test
    void shouldSendSingleDatagramMessage() {
        Message message = Message.datagram("asdasd")
                .from("123123123")
                .to("12312312")
                .build();
        client.send(message);

        assertThat(mockSmppServer).hasSingleMessage()
                .hasEsmClass(SmppConstants.ESM_CLASS_MM_DATAGRAM)
                .hasDest("12312312")
                .hasSource("123123123")
                .hasText("asdasd")
                .notSilent()
                .doesNotHaveDeliveryReport();
    }

    @Test
    void shouldSendSingleSilentMessage() {
        Message message = Message.silent("asdasd")
                .from("123123123")
                .to("12312312")
                .build();
        client.send(message);

        assertThat(mockSmppServer).hasSingleMessage()
                .hasEsmClass(SmppConstants.ESM_CLASS_MM_STORE_FORWARD)
                .hasDest("12312312")
                .hasSource("123123123")
                .hasText("asdasd")
                .silent()
                .doesNotHaveDeliveryReport();
    }

    @Test
    void shouldSendSingleCancelMessage() {
        CancelMessage cancelMessage = new CancelMessage("123", "123123123", "12312312");
        client.cancel(cancelMessage);

        assertThat(mockSmppServer).hasSingleCancelMessage()
                .hasDest("12312312")
                .hasSource("123123123")
                .hasId("123");
    }

    @Test
    void shouldSendSingleDatagramAndCancelMessages() {
        Message message = Message.datagram("asdasd")
                .from("123123123")
                .to("12312312")
                .build();
        client.send(message);

        CancelMessage cancelMessage = new CancelMessage("123", "123123123", "12312312");
        client.cancel(cancelMessage);

        assertThat(mockSmppServer).messages()
                .allSatisfy(pduRequest -> {
                    if (pduRequest instanceof SubmitSm) {
                        assertThat((SubmitSm) pduRequest)
                                .hasEsmClass(SmppConstants.ESM_CLASS_MM_DATAGRAM)
                                .hasDest("12312312")
                                .hasSource("123123123")
                                .hasText("asdasd")
                                .notSilent()
                                .doesNotHaveDeliveryReport();
                    } else if (pduRequest instanceof CancelSm) {
                        assertThat((CancelSm) pduRequest)
                                .hasDest("12312312")
                                .hasSource("123123123")
                                .hasId("123");
                    } else {
                        log.debug("ignore " + pduRequest);
                    }
                });
    }

    private SenderClient createDefaultSenderClient() {
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(credentials);
        smsc.setMaxTry(5);
        return new ClientFactory().standardSender("test", new SmppProperties.Defaults(),
                smsc, new DefaultTypeOfAddressParser());
    }

    private static SmppProperties.Credentials credentials() {
        SmppProperties.Credentials credentials = new SmppProperties.Credentials();
        credentials.setPort(findRandomOpenPortOnAllLocalInterfaces());
        credentials.setHost("localhost");
        credentials.setUsername("username");
        credentials.setPassword("password");
        return credentials;
    }

    private static Integer findRandomOpenPortOnAllLocalInterfaces() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to find port", e);
        }
    }
}
