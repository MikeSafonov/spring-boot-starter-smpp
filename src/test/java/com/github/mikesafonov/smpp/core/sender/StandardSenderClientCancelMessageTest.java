package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.CancelSm;
import com.cloudhopper.smpp.pdu.CancelSmResp;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.github.mikesafonov.smpp.core.connection.TransmitterConfiguration;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.CancelMessageResponse;
import com.github.mikesafonov.smpp.core.dto.MessageErrorInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class StandardSenderClientCancelMessageTest extends BaseStandardSenderClientTest {


    private static Stream<WindowFuture<Integer, PduRequest, PduResponse>> failWindowFutureProvider(){
        return Stream.of(
                failWindowsFuture(false, true, true),
                failWindowsFuture(true, false, true),
                failWindowsFuture(true, true, false)
        );
    }

    @Test
    void shouldReturnErrorBecauseMessageIdIsEmpty() {
        CancelMessage originalMessage = new CancelMessage(null, randomString(), randomString());
        MessageErrorInformation messageErrorInformation = new MessageErrorInformation(0, "Empty message id");
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        CancelMessageResponse messageResponse = senderClient.cancel(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscConnectionId());
        assertFalse(messageResponse.isSuccess());
        assertEquals(messageErrorInformation, messageResponse.getMessageErrorInformation());
    }

    @Test
    void shouldCancelMessage() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException, RecoverablePduException {
        SmppSession session = mock(SmppSession.class);
        CancelMessage cancelMessage = new CancelMessage(randomString(), randomString(), randomString());
        CancelSm cancelSm = new CancelSm();
        CancelSmResp cancelSmResp = new CancelSmResp();
        cancelSmResp.setCommandStatus(SmppConstants.STATUS_OK);

        WindowFuture<Integer, PduRequest, PduResponse> futureResponse = successWindowsFuture();
        when(futureResponse.getResponse()).thenReturn(cancelSmResp);
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(messageBuilder.createCancelSm(cancelMessage)).thenReturn(cancelSm);
        when(session.sendRequestPdu(eq(cancelSm), anyLong(), anyBoolean())).thenReturn(futureResponse);

        CancelMessageResponse cancelMessageResponse = senderClient.cancel(cancelMessage);

        assertEquals(cancelMessage, cancelMessageResponse.getOriginal());
        assertTrue(cancelMessageResponse.isSuccess());
        assertNull(cancelMessageResponse.getMessageErrorInformation());
        assertEquals(senderClient.getId(), cancelMessageResponse.getSmscConnectionId());
    }

    @Test
    void shouldFailCancel() throws InterruptedException, RecoverablePduException, SmppChannelException, UnrecoverablePduException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        CancelMessage cancelMessage = new CancelMessage(randomString(), randomString(), randomString());
        CancelSm cancelSm = new CancelSm();
        CancelSmResp cancelSmResp = new CancelSmResp();
        cancelSmResp.setCommandStatus(SmppConstants.STATUS_CANCELFAIL);
        cancelSmResp.setResultMessage(randomString());

        WindowFuture<Integer, PduRequest, PduResponse> futureResponse = successWindowsFuture();
        when(futureResponse.getResponse()).thenReturn(cancelSmResp);
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(messageBuilder.createCancelSm(cancelMessage)).thenReturn(cancelSm);
        when(session.sendRequestPdu(eq(cancelSm), anyLong(), anyBoolean())).thenReturn(futureResponse);

        CancelMessageResponse cancelMessageResponse = senderClient.cancel(cancelMessage);

        assertEquals(cancelMessage, cancelMessageResponse.getOriginal());
        assertFalse(cancelMessageResponse.isSuccess());
        assertEquals(senderClient.getId(), cancelMessageResponse.getSmscConnectionId());
        assertEquals(cancelSmResp.getResultMessage(), cancelMessageResponse.getMessageErrorInformation().getErrorMessage());
        assertEquals(101, cancelMessageResponse.getMessageErrorInformation().getErrorCode());
    }

    @Test
    void shouldFailCancelBecauseUnexpectedException() throws UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        CancelMessage cancelMessage = new CancelMessage(randomString(), randomString(), randomString());
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(messageBuilder.createCancelSm(cancelMessage)).thenThrow(new RuntimeException());

        CancelMessageResponse cancelMessageResponse = senderClient.cancel(cancelMessage);

        assertEquals(cancelMessage, cancelMessageResponse.getOriginal());
        assertFalse(cancelMessageResponse.isSuccess());
        assertEquals(senderClient.getId(), cancelMessageResponse.getSmscConnectionId());
        assertEquals("Unexpected exception", cancelMessageResponse.getMessageErrorInformation().getErrorMessage());
        assertEquals(102, cancelMessageResponse.getMessageErrorInformation().getErrorCode());
    }

    @Test
    void shouldFailCancelBecauseExpectedException() throws InterruptedException, RecoverablePduException, SmppChannelException, UnrecoverablePduException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        CancelMessage cancelMessage = new CancelMessage(randomString(), randomString(), randomString());
        CancelSm cancelSm = new CancelSm();
        CancelSmResp cancelSmResp = new CancelSmResp();
        cancelSmResp.setCommandStatus(SmppConstants.STATUS_CANCELFAIL);
        cancelSmResp.setResultMessage(randomString());

        WindowFuture<Integer, PduRequest, PduResponse> futureResponse = successWindowsFuture();
        when(futureResponse.getResponse()).thenReturn(cancelSmResp);
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(messageBuilder.createCancelSm(cancelMessage)).thenReturn(cancelSm);
        when(session.sendRequestPdu(eq(cancelSm), anyLong(), anyBoolean())).thenThrow(new RecoverablePduException("RecoverablePduException"));

        CancelMessageResponse cancelMessageResponse = senderClient.cancel(cancelMessage);

        assertEquals(cancelMessage, cancelMessageResponse.getOriginal());
        assertFalse(cancelMessageResponse.isSuccess());
        assertEquals(senderClient.getId(), cancelMessageResponse.getSmscConnectionId());
        assertEquals("RecoverablePduException", cancelMessageResponse.getMessageErrorInformation().getErrorMessage());
        assertEquals(101, cancelMessageResponse.getMessageErrorInformation().getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("failWindowFutureProvider")
    void shouldFailCancelBecauseRequestFailed(WindowFuture<Integer, PduRequest, PduResponse> futureResponse)
            throws RecoverablePduException, InterruptedException, SmppChannelException,
            UnrecoverablePduException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        CancelMessage cancelMessage = new CancelMessage(randomString(), randomString(), randomString());
        CancelSm cancelSm = new CancelSm();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(messageBuilder.createCancelSm(cancelMessage)).thenReturn(cancelSm);
        when(session.sendRequestPdu(eq(cancelSm), anyLong(), anyBoolean())).thenReturn(futureResponse);

        assertDoesNotThrow(() -> senderClient.setup());

        CancelMessageResponse cancelMessageResponse = senderClient.cancel(cancelMessage);

        assertEquals(cancelMessage, cancelMessageResponse.getOriginal());
        assertFalse(cancelMessageResponse.isSuccess());
        assertEquals(senderClient.getId(), cancelMessageResponse.getSmscConnectionId());
        assertEquals("Unable to get response", cancelMessageResponse.getMessageErrorInformation().getErrorMessage());
        assertEquals(101, cancelMessageResponse.getMessageErrorInformation().getErrorCode());
    }
}
