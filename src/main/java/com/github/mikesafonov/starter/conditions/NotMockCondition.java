package com.github.mikesafonov.starter.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Mike Safonov
 */
public class NotMockCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        TestCondition testCondition = new TestCondition();
        StandardCondition standardCondition = new StandardCondition();
        return testCondition.matches(context, metadata) || standardCondition.matches(context, metadata);
    }
}
