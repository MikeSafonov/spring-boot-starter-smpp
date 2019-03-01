package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Mike Safonov
 */
public class SenderClientFactoryBean implements FactoryBean<SenderClient> {
    @Override
    public SenderClient getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
