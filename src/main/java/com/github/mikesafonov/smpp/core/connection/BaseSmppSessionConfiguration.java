package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSessionConfiguration;

public abstract class BaseSmppSessionConfiguration extends SmppSessionConfiguration {
    public abstract String configInformation();
}
