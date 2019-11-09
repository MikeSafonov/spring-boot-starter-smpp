package com.github.mikesafonov.smpp.server;

import com.cloudhopper.smpp.PduAsyncResponse;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class QueueSmppSessionHandler implements SmppSessionHandler {

    private final BlockingQueue<PduRequest> receivedPduRequests;

    public QueueSmppSessionHandler() {
        this.receivedPduRequests = new LinkedBlockingQueue<PduRequest>();
    }

    public BlockingQueue<PduRequest> getReceivedPduRequests() {
        return receivedPduRequests;
    }

    @Override
    public void fireChannelUnexpectedlyClosed() {
        // nothing
    }

    @Override
    public PduResponse firePduRequestReceived(PduRequest pduRequest) {
        receivedPduRequests.add(pduRequest);
        return pduRequest.createResponse();
    }

    @Override
    public void firePduRequestExpired(PduRequest pduRequest) {
        log.info(pduRequest.toString());
    }

    @Override
    public void fireExpectedPduResponseReceived(PduAsyncResponse pduAsyncResponse) {
        log.info(pduAsyncResponse.toString());
    }

    @Override
    public void fireUnexpectedPduResponseReceived(PduResponse pduResponse) {
        log.info(pduResponse.toString());
    }

    @Override
    public void fireUnrecoverablePduException(UnrecoverablePduException e) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void fireRecoverablePduException(RecoverablePduException e) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void fireUnknownThrowable(Throwable t) {
        log.error(t.getMessage(), t);
    }

    @Override
    public String lookupResultMessage(int commandStatus) {
        return null;
    }

    @Override
    public String lookupTlvTagName(short tag) {
        return null;
    }
}
