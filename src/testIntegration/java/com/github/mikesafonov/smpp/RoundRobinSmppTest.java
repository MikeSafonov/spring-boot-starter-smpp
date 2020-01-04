package com.github.mikesafonov.smpp;

import com.cloudhopper.smpp.type.SmppChannelException;
import com.github.mikesafonov.smpp.config.SmppAutoConfiguration;
import com.github.mikesafonov.smpp.config.SmscConnectionsHolder;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.smpp.core.reciever.ResponseClient;
import com.github.mikesafonov.smpp.core.reciever.ResponseSmppSessionHandler;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import com.github.mikesafonov.smpp.server.MockSmppServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.github.mikesafonov.smpp.asserts.SmppAssertions.assertThat;

@SpringBootTest(classes = {RoundRobinApplicationConfiguration.class, SmppAutoConfiguration.class})
@TestPropertySource(
        locations = "classpath:application.properties")
public class RoundRobinSmppTest {
    @Autowired
    private RoundRobinApplicationService roundRobinApplicationService;
    @Autowired
    private SmscConnectionsHolder smscConnectionsHolder;
    @Autowired
    private DeliveryReportConsumer deliveryReportConsumer;

    private MockSmppServer one;
    private MockSmppServer two;

    @BeforeEach
    void runMockServer() throws SmppChannelException {
        one = new MockSmppServer(1111, "user", "pass");
        one.start();
        two = new MockSmppServer(2222, "user2", "pass2");
        two.start();

        // setup clients after MockSmppServer`s are up and running
        smscConnectionsHolder.getConnections()
                .forEach(smscConnection -> setupClients(smscConnection.getSenderClient(), smscConnection.getResponseClient().orElse(null)));
    }

    private void setupClients(SenderClient senderClient, ResponseClient responseClient) {
        senderClient.setup();
        if (responseClient != null) {
            ResponseSmppSessionHandler responseSmppSessionHandler = new ResponseSmppSessionHandler(responseClient, deliveryReportConsumer);
            responseClient.setup(responseSmppSessionHandler);
        }
    }

    @AfterEach
    void stopMockServer() {
        one.stop();
        two.stop();
    }

    @Test
    void shouldDo() {
        roundRobinApplicationService.sendMessage("one", "two", "one message");
        roundRobinApplicationService.sendMessage("two", "one", "two message");

        assertThat(one).hasSingleMessage()
                .hasDeliveryReport()
                .hasDest("two")
                .hasSource("one")
                .hasText("one message");

        assertThat(two).hasSingleMessage()
                .hasDeliveryReport()
                .hasDest("one")
                .hasSource("two")
                .hasText("two message");
    }
}
