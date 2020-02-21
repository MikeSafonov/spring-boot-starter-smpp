package com.github.mikesafonov.smpp.core.connection;

@FunctionalInterface
public interface SessionReconnector {
    void reconnect();
}
