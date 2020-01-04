package com.github.mikesafonov.smpp.config;

import lombok.Value;

import java.util.List;

@Value
public class SmscConnectionsHolder {
    private final List<SmscConnection> connections;
}
