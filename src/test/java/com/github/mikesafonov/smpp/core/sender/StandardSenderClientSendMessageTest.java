package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.*;
import com.github.mikesafonov.smpp.core.connection.TransmitterConfiguration;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageErrorInformation;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.dto.MessageType;
import com.github.mikesafonov.smpp.core.exceptions.IllegalAddressException;
import com.github.mikesafonov.smpp.core.exceptions.SmppMessageBuildingException;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.randomString;
import static com.github.mikesafonov.smpp.util.Randomizer.randomTransmitterConfiguration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class StandardSenderClientSendMessageTest extends BaseStandardSenderClientTest {

    @Test
    void shouldReturnErrorBecauseMessageIsEmpty() {
        Message originalMessage = new Message(null, randomString(), randomString(), randomString(), MessageType.SIMPLE);
        MessageErrorInformation messageErrorInformation = new MessageErrorInformation(0, "Empty message text");
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertNull(messageResponse.getSmscMessageID());
        assertFalse(messageResponse.isSent());
        assertEquals(messageErrorInformation, messageResponse.getMessageErrorInformation());
    }

    @Test
    void successSendMessage() throws RecoverablePduException, UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        Message originalMessage = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        String smscMessageId = randomString();
        SubmitSm submitSm = new SubmitSm();
        SubmitSmResp submitSmResp = new SubmitSmResp();
        submitSmResp.setCommandStatus(SmppConstants.STATUS_OK);
        submitSmResp.setMessageId(smscMessageId);
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(session.submit(eq(submitSm), anyLong())).thenReturn(submitSmResp);
        when(messageBuilder.createSubmitSm(eq(originalMessage), anyBoolean())).thenReturn(submitSm);

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertEquals(smscMessageId, messageResponse.getSmscMessageID());
        assertNull(messageResponse.getMessageErrorInformation());
    }

    @Test
    void failSendMessage() throws RecoverablePduException, UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        Message originalMessage = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        String smscMessageId = randomString();
        SubmitSm submitSm = new SubmitSm();
        SubmitSmResp submitSmResp = new SubmitSmResp();
        submitSmResp.setCommandStatus(SmppConstants.STATUS_INVEXPIRY);
        submitSmResp.setMessageId(smscMessageId);
        submitSmResp.setResultMessage(randomString());
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(session.submit(eq(submitSm), anyLong())).thenReturn(submitSmResp);
        when(messageBuilder.createSubmitSm(eq(originalMessage), anyBoolean())).thenReturn(submitSm);

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertNull( messageResponse.getSmscMessageID());
        assertEquals(submitSmResp.getResultMessage(), messageResponse.getMessageErrorInformation().getErrorMessage());
        assertEquals(101, messageResponse.getMessageErrorInformation().getErrorCode());
    }

    @Test
    void failSendMessageBecauseThrowException() throws RecoverablePduException, UnrecoverablePduException, SmppChannelException, InterruptedException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        Message originalMessage = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        String smscMessageId = randomString();
        SubmitSm submitSm = new SubmitSm();
        SubmitSmResp submitSmResp = new SubmitSmResp();
        submitSmResp.setCommandStatus(SmppConstants.STATUS_INVEXPIRY);
        submitSmResp.setMessageId(smscMessageId);
        submitSmResp.setResultMessage(randomString());
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(session.submit(eq(submitSm), anyLong())).thenThrow(SmppInvalidArgumentException.class);
        when(messageBuilder.createSubmitSm(eq(originalMessage), anyBoolean())).thenReturn(submitSm);

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertNull( messageResponse.getSmscMessageID());
        assertEquals("Invalid param", messageResponse.getMessageErrorInformation().getErrorMessage());
        assertEquals(101, messageResponse.getMessageErrorInformation().getErrorCode());
    }

    @Test
    void failSendMessageBecauseThrowUnexpectedException() throws RecoverablePduException, UnrecoverablePduException,
            SmppChannelException, InterruptedException, SmppTimeoutException {
        SmppSession session = mock(SmppSession.class);
        Message originalMessage = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        String smscMessageId = randomString();
        SubmitSm submitSm = new SubmitSm();
        SubmitSmResp submitSmResp = new SubmitSmResp();
        submitSmResp.setCommandStatus(SmppConstants.STATUS_INVEXPIRY);
        submitSmResp.setMessageId(smscMessageId);
        submitSmResp.setResultMessage(randomString());
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(session.submit(eq(submitSm), anyLong())).thenThrow(RuntimeException.class);
        when(messageBuilder.createSubmitSm(eq(originalMessage), anyBoolean())).thenReturn(submitSm);

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertNull( messageResponse.getSmscMessageID());
        assertEquals("Cant send message", messageResponse.getMessageErrorInformation().getErrorMessage());
        assertEquals(102, messageResponse.getMessageErrorInformation().getErrorCode());
    }

    @Test
    void failSendMessageBecauseThrowIllegalAddressException() throws UnrecoverablePduException,
            SmppChannelException, InterruptedException, SmppTimeoutException, RecoverablePduException {
        SmppSession session = mock(SmppSession.class);
        Message originalMessage = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        IllegalAddressException illegalAddressException = new IllegalAddressException(randomString());
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(messageBuilder.createSubmitSm(eq(originalMessage), anyBoolean())).thenThrow(illegalAddressException);

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertEquals(101, messageResponse.getMessageErrorInformation().getErrorCode());
        assertEquals(illegalAddressException.getMessage(), messageResponse.getMessageErrorInformation().getErrorMessage());
        assertNull(messageResponse.getSmscMessageID());
    }

    @Test
    void failSendMessageBecauseThrowSmppMessageBuildingException() throws UnrecoverablePduException,
            SmppChannelException, InterruptedException, SmppTimeoutException, RecoverablePduException {
        SmppSession session = mock(SmppSession.class);
        Message originalMessage = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        SmppMessageBuildingException exception = new SmppMessageBuildingException();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        when(connectionManager.getConfiguration()).thenReturn(transmitterConfiguration);
        when(connectionManager.getSession()).thenReturn(session);
        when(messageBuilder.createSubmitSm(eq(originalMessage), anyBoolean())).thenThrow(exception);

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertEquals(101, messageResponse.getMessageErrorInformation().getErrorCode());
        assertEquals("Invalid param", messageResponse.getMessageErrorInformation().getErrorMessage());
        assertNull(messageResponse.getSmscMessageID());
    }
}
