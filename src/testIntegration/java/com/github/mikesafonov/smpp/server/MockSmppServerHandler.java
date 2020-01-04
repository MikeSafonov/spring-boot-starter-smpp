package com.github.mikesafonov.smpp.server;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MockSmppServerHandler implements SmppServerHandler {
    private final Set<SmppServerSession> sessions;
    private final QueueSmppSessionHandler sessionHandler;
    private final String systemId;
    private final String password;

    public MockSmppServerHandler(String systemId, String password) {
        this.systemId = systemId;
        this.password = password;
        sessions = new HashSet<>();
        sessionHandler = new QueueSmppSessionHandler();
    }

    @Override
    public void sessionBindRequested(Long sessionId, SmppSessionConfiguration sessionConfiguration, BaseBind bindRequest) throws SmppProcessingException {
        verifySystemId(bindRequest.getSystemId());
        verifyPassword(bindRequest.getPassword());
    }

    @Override
    public void sessionCreated(Long sessionId, SmppServerSession session, BaseBindResp preparedBindResponse) {
        sessions.add(session);
        session.serverReady(sessionHandler);
        log.info("session " + sessionId + " created");
    }

    @Override
    public void sessionDestroyed(Long sessionId, SmppServerSession session) {
        sessions.remove(session);
        log.info("session " + sessionId + " destroyed");
    }

    public Set<SmppServerSession> getSessions() {
        return sessions;
    }

    public QueueSmppSessionHandler getSessionHandler() {
        return sessionHandler;
    }

    public int countTotalMessages() {
        return sessionHandler.countTotalMessages();
    }

    public String getSystemId() {
        return systemId;
    }

    public String getPassword() {
        return password;
    }

    private void verifySystemId(String incomingSystemId) throws SmppProcessingException {
        if (!systemId.equals(incomingSystemId)) {
            throw new SmppProcessingException(SmppConstants.STATUS_INVSYSID);
        }
    }

    private void verifyPassword(String incomingPassword) throws SmppProcessingException {
        if (!password.equals(incomingPassword)) {
            throw new SmppProcessingException(SmppConstants.STATUS_INVPASWD);
        }
    }
}
