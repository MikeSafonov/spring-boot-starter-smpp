package com.github.mikesafonov.smpp.config;

import lombok.Value;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 *
 * @author Mike Safonov
 * @author Mikhail Epatko
 */
@Value
public class SmscConnectionsHolder {
    private final List<SmscConnection> connections;

    @PreDestroy
    public void closeConnections() {
        connections.forEach(SmscConnection::closeConnection);
    }
}
