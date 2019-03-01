package com.github.mikesafonov.starter.conditions;

import com.github.mikesafonov.starter.StarterMode;

/**
 * @author Mike Safonov
 */
public class TestCondition extends ModeCondition {
    public TestCondition() {
        super(StarterMode.TEST);
    }
}
