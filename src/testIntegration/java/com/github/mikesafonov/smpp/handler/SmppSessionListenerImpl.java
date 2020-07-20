package com.github.mikesafonov.smpp.handler;

import com.cloudhopper.smpp.PduAsyncResponse;
import com.cloudhopper.smpp.SmppSessionListener;
import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Safonov
 */
public class SmppSessionListenerImpl implements SmppSessionListener {
    @Getter
    private final List<Pdu> pduList = new ArrayList<>();

    @Override
    public boolean firePduReceived(Pdu pdu) {
        return pduList.add(pdu);
    }

    @Override
    public boolean firePduDispatch(Pdu pdu) {
        return true;
    }

    @Override
    public void fireChannelUnexpectedlyClosed() {

    }

    @Override
    public PduResponse firePduRequestReceived(PduRequest pduRequest) {
        return null;
    }

    @Override
    public void firePduRequestExpired(PduRequest pduRequest) {

    }

    @Override
    public void fireExpectedPduResponseReceived(PduAsyncResponse pduAsyncResponse) {

    }

    @Override
    public void fireUnexpectedPduResponseReceived(PduResponse pduResponse) {

    }

    @Override
    public void fireUnrecoverablePduException(UnrecoverablePduException e) {

    }

    @Override
    public void fireRecoverablePduException(RecoverablePduException e) {

    }

    @Override
    public void fireUnknownThrowable(Throwable t) {

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
