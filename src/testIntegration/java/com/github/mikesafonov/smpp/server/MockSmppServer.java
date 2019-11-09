package com.github.mikesafonov.smpp.server;

import com.cloudhopper.smpp.SmppServerConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppServer;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.github.mikesafonov.smpp.config.SmppProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
public class MockSmppServer {
    private static final int DEFAULT_PORT = 2077;
    static final String DEFAULT_SYSTEM_ID = "mockSmppServer";
    static final String DEFAULT_PASSWORD = "password";

    private final int port;
    private final String systemId;
    private final MockSmppServerHandler handler;
    private final DefaultSmppServer smppServer;

    public MockSmppServer() {
        this(DEFAULT_PORT);
    }

    public MockSmppServer(int port) {
        this(port, DEFAULT_SYSTEM_ID, DEFAULT_PASSWORD);
    }

    public MockSmppServer(String systemId, String password) {
        this(DEFAULT_PORT, systemId, password);
    }

    public MockSmppServer(SmppProperties.Credentials credentials) {
        this(credentials.getPort(), credentials.getUsername(), credentials.getPassword());
    }

    public MockSmppServer(int port, String systemId, String password) {
        this.port = port;
        this.systemId = systemId;
        SmppServerConfiguration configuration = new SmppServerConfiguration();
        configuration.setPort(port);
        configuration.setSystemId(systemId);

        this.handler = new MockSmppServerHandler(systemId, password);
        smppServer = new DefaultSmppServer(configuration, handler, Executors.newCachedThreadPool());
    }

    public void start() throws SmppChannelException {
        smppServer.start();
    }

    public void stop() {
        smppServer.stop();
    }

    public int getPort() {
        return port;
    }

    public String getSystemId() {
        return systemId;
    }

    public int countMessages() {
        return handler.getSessionHandler().getReceivedPduRequests().size();
    }

    public List<PduRequest> getMessages() {
        return new ArrayList<>(handler.getSessionHandler().getReceivedPduRequests());
    }
}
