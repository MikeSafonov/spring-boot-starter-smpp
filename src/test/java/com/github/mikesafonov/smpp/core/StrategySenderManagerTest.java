package com.github.mikesafonov.smpp.core;

import com.github.mikesafonov.smpp.api.IndexDetectionStrategy;
import com.github.mikesafonov.smpp.api.RandomIndexDetectionStrategy;
import com.github.mikesafonov.smpp.api.RoundRobinIndexDetectionStrategy;
import com.github.mikesafonov.smpp.api.StrategySenderManager;
import com.github.mikesafonov.smpp.config.SmscConnection;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.github.mikesafonov.smpp.core.util.Randomizer.randomString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Mike Safonov
 */
class StrategySenderManagerTest {

    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new StrategySenderManager(null, mock(IndexDetectionStrategy.class)));
        assertThrows(NullPointerException.class, () -> new StrategySenderManager(emptyList(), null));
    }

    @Test
    void shouldReturnEmpty() {
        IndexDetectionStrategy indexDetectionStrategy = mock(IndexDetectionStrategy.class);
        StrategySenderManager strategySenderManager = new StrategySenderManager(emptyList(), indexDetectionStrategy);

        assertEquals(Optional.empty(), strategySenderManager.getByName(randomString()));
        verifyZeroInteractions(indexDetectionStrategy);
    }

    @Test
    void shouldReturnNonEmpty() {
        IndexDetectionStrategy indexDetectionStrategy = mock(IndexDetectionStrategy.class);
        String expectedName = randomString();
        SmscConnection expectedConnection = new SmscConnection(expectedName, mock(SenderClient.class));
        List<SmscConnection> smscConnections = asList(
                expectedConnection,
                new SmscConnection(randomString(), mock(SenderClient.class))
        );
        StrategySenderManager strategySenderManager = new StrategySenderManager(smscConnections, indexDetectionStrategy);

        assertEquals(Optional.empty(), strategySenderManager.getByName(randomString()));
        assertEquals(expectedConnection.getSenderClient(), strategySenderManager.getByName(expectedName).get());
        verifyZeroInteractions(indexDetectionStrategy);
    }

    @Test
    void shouldReturnEmptyBecauseNoConnections() {
        IndexDetectionStrategy indexDetectionStrategy = mock(IndexDetectionStrategy.class);
        StrategySenderManager strategySenderManager = new StrategySenderManager(emptyList(), mock(IndexDetectionStrategy.class));

        assertEquals(Optional.empty(), strategySenderManager.getClient());
        verifyZeroInteractions(indexDetectionStrategy);
    }

    @Test
    void shouldReturnFirstBecauseOnlyOneConnection() {
        IndexDetectionStrategy indexDetectionStrategy = mock(IndexDetectionStrategy.class);
        SmscConnection expectedConnection = new SmscConnection(randomString(), mock(SenderClient.class));
        List<SmscConnection> smscConnections = asList(
                expectedConnection
        );
        StrategySenderManager strategySenderManager = new StrategySenderManager(smscConnections, mock(IndexDetectionStrategy.class));

        assertEquals(expectedConnection.getSenderClient(), strategySenderManager.getClient().get());
        verifyZeroInteractions(indexDetectionStrategy);
    }

    @Nested
    class RandomIndexDetectionStrategyTest {

        @Test
        void shouldReturnNonEmptyClient() {
            RandomIndexDetectionStrategy strategy = new RandomIndexDetectionStrategy();
            List<SmscConnection> smscConnections = asList(
                    new SmscConnection(randomString(), mock(SenderClient.class)),
                    new SmscConnection(randomString(), mock(SenderClient.class))
            );

            StrategySenderManager strategySenderManager = new StrategySenderManager(smscConnections, strategy);

            SenderClient client = strategySenderManager.getClient().get();

            assertTrue(smscConnections.stream().map(SmscConnection::getSenderClient).collect(toList()).contains(client));

        }

    }

    @Nested
    class RoundRobinIndexDetectionStrategyTest {
        @Test
        void shouldReturnNonEmptyClient() {
            RoundRobinIndexDetectionStrategy strategy = new RoundRobinIndexDetectionStrategy();
            List<SmscConnection> smscConnections = asList(
                    new SmscConnection(randomString(), mock(SenderClient.class)),
                    new SmscConnection(randomString(), mock(SenderClient.class))
            );

            StrategySenderManager strategySenderManager = new StrategySenderManager(smscConnections, strategy);

            SenderClient client = strategySenderManager.getClient().get();

            assertTrue(smscConnections.stream().map(SmscConnection::getSenderClient).collect(toList()).contains(client));

        }
    }
}
