package com.github.mikesafonov.smpp.asserts;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.CancelSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.sender.MessageBuilder;
import com.github.mikesafonov.smpp.server.MockSmppServer;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

public class SmppAssert extends AbstractAssert<SmppAssert, MockSmppServer> {
    public SmppAssert(MockSmppServer mockSmppServer) {
        super(mockSmppServer, SmppAssert.class);
    }

    public SmppAssert receiveMessagesCount(int size) {
        isNotNull();
        checkMessagesCount(actual.getMessages(), size);
        return this;
    }

    public SubmitSmAssert hasSingleMessage() {
        return new SubmitSmAssert(checkAndGetSubmitSm());
    }

    public CancelSmAssert hasSingleCancelMessage() {
        return new CancelSmAssert(checkAndGetCancelSm());
    }

    public PduRequestListAssert messages() {
        return new PduRequestListAssert(actual.getMessages());
    }

    public SmppAssert hasExactlyMessage(Message message) {
        switch (message.getMessageType()) {
            case SILENT: {
                return hasExactlySilent(message);
            }
            case SIMPLE: {
                return hasExactlySimple(message);
            }
            case DATAGRAM: {
                return hasExactlyDatagram(message);
            }
            default: {
                failWithMessage("");
            }
        }
        return this;
    }

    public SmppAssert hasExactlySilent(Message message) {
        baseMessageTypeCheck(message, SmppConstants.ESM_CLASS_MM_STORE_FORWARD, MessageBuilder.SILENT_CODING);
        return this;
    }

    public SmppAssert hasExactlyDatagram(Message message) {
        baseMessageTypeCheck(message, SmppConstants.ESM_CLASS_MM_DATAGRAM, SmppConstants.DATA_CODING_DEFAULT);
        return this;
    }

    public SmppAssert hasExactlySimple(Message message) {
        baseMessageTypeCheck(message, SmppConstants.ESM_CLASS_MM_STORE_FORWARD, SmppConstants.DATA_CODING_DEFAULT);
        return this;
    }

    private void baseMessageTypeCheck(Message message, byte expectedEsmClass, byte expectedCoding) {
        SubmitSm submitSm = checkAndGetSubmitSm();
        if (submitSm.getEsmClass() != expectedEsmClass) {
            failWithMessage("Expected esm class <%s> but was <%s>", expectedEsmClass,
                    submitSm.getEsmClass());
        }
        if (submitSm.getDataCoding() != expectedCoding) {
            failWithMessage("Expected data coding <%s> but was <%s>", expectedCoding,
                    submitSm.getDataCoding());
        }
        checkEquals(submitSm, message);
    }

    private void checkMessagesCount(List<? extends PduRequest> messages, int expectedCount) {
        if (messages.size() != expectedCount) {
            failWithMessage("Expected messages size to be <%s> but was <%s>", expectedCount, messages.size());
        }
    }

    private void checkEquals(SubmitSm submitSm, Message message) {
        if (!message.getMsisdn().equals(submitSm.getDestAddress().getAddress())) {
            failWithMessage("Expected message <%s> but was <%s>", message.toString(), submitSm.toString());
        }
        if (!message.getSource().equals(submitSm.getSourceAddress().getAddress())) {
            failWithMessage("Expected message <%s> but was <%s>", message.toString(), submitSm.toString());
        }
        String textMessage = new String(submitSm.getShortMessage());
        if (!message.getText().equals(textMessage)) {
            failWithMessage("Expected message <%s> but was <%s>", message.toString(), submitSm.toString());
        }
    }

    private SubmitSm checkAndGetSubmitSm() {
        isNotNull();
        List<SubmitSm> submitSmMessages = actual.getSubmitSmMessages();
        checkMessagesCount(submitSmMessages, 1);
        return submitSmMessages.get(0);
    }

    private CancelSm checkAndGetCancelSm() {
        isNotNull();
        List<CancelSm> cancelSmMessages = actual.getCancelSmMessages();
        checkMessagesCount(cancelSmMessages, 1);
        return cancelSmMessages.get(0);
    }
}
