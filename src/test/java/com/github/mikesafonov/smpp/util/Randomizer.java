package com.github.mikesafonov.smpp.util;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.devskiller.jfairy.Fairy;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.connection.ReceiverConfiguration;
import com.github.mikesafonov.smpp.core.connection.TransceiverConfiguration;
import com.github.mikesafonov.smpp.core.connection.TransmitterConfiguration;
import lombok.experimental.UtilityClass;

import java.time.Duration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class Randomizer {
    private static final Fairy FAIRY = Fairy.create();

    public static int randomPort() {
        return FAIRY.baseProducer().randomInt(9999);
    }

    public static int randomInt() {
        return FAIRY.baseProducer().randomInt(9999);
    }

    public static int randomPositive(int max) {
        return FAIRY.baseProducer().randomInt(max) + 1;
    }

    public static long randomLong() {
        return FAIRY.baseProducer().randomBetween(0, 9999);
    }

    public static boolean randomBoolean() {
        return FAIRY.baseProducer().trueOrFalse();
    }

    public static String randomString() {
        return FAIRY.textProducer().latinWord(10);
    }

    public static String randomIp() {
        return FAIRY.networkProducer().ipAddress();
    }

    public static Duration randomDuration() {
        return Duration.ofSeconds(randomLong());
    }

    public static WindowFuture<Integer, PduRequest, PduResponse> successWindowsFuture() throws InterruptedException {
        WindowFuture<Integer, PduRequest, PduResponse> futureResponse = mock(WindowFuture.class);
        when(futureResponse.await()).thenReturn(true);
        when(futureResponse.isDone()).thenReturn(true);
        when(futureResponse.isSuccess()).thenReturn(true);
        return futureResponse;
    }

    public static WindowFuture<Integer, PduRequest, PduResponse> failWindowsFuture(boolean await, boolean done, boolean success) {
        try {
            WindowFuture<Integer, PduRequest, PduResponse> futureResponse = mock(WindowFuture.class);
            when(futureResponse.await()).thenReturn(await);
            when(futureResponse.isDone()).thenReturn(done);
            when(futureResponse.isSuccess()).thenReturn(success);
            return futureResponse;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static SmppProperties.Credentials randomCredentials() {
        SmppProperties.Credentials credentials = new SmppProperties.Credentials();
        credentials.setHost(randomIp());
        credentials.setPort(randomPort());
        credentials.setUsername(randomString());
        credentials.setPassword(randomString());
        return credentials;
    }

    public static TransmitterConfiguration randomTransmitterConfiguration() {
        SmppProperties.Credentials credentials = randomCredentials();
        return new TransmitterConfiguration(randomString(), credentials, randomBoolean(), randomBoolean(), randomInt(),
            randomString());
    }

    public static TransceiverConfiguration randomTransceiverConfiguration() {
        SmppProperties.Credentials credentials = randomCredentials();
        return new TransceiverConfiguration(randomString(), credentials, randomBoolean(), randomBoolean(), randomInt(),
            randomString());
    }

    public static ReceiverConfiguration randomReceiverConfiguration() {
        SmppProperties.Credentials credentials = randomCredentials();
        return new ReceiverConfiguration(randomString(), credentials, randomBoolean(), randomBoolean());
    }
}
