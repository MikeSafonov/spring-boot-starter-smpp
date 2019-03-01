package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class SenderClientFactoryBean implements FactoryBean<List<SenderClient>> {

    private final SmppProperties smppProperties;


    @Override
    public List<SenderClient> getObject() throws Exception {
        //TODO: implements sender client registry.
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return List.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
