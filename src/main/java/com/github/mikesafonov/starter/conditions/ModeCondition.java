package com.github.mikesafonov.starter.conditions;

import com.github.mikesafonov.starter.StarterMode;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Mike Safonov
 */
abstract public class ModeCondition implements Condition {
    private StarterMode expectedMode;

    public ModeCondition(StarterMode expectedMode) {
        this.expectedMode = expectedMode;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        StarterMode starterMode = StarterMode.from(context.getEnvironment().getProperty("spring.smpp.starterMode"));
        return starterMode == expectedMode;
    }
}
