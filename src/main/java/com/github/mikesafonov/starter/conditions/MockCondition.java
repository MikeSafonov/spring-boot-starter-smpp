package com.github.mikesafonov.starter.conditions;


import com.github.mikesafonov.starter.StarterMode;

/**
 * @author Mike Safonov
 */
public class MockCondition extends ModeCondition {
    public MockCondition() {
        super(StarterMode.MOCK);
    }
}
