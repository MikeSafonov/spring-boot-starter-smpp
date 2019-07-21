package com.github.mikesafonov.smpp.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoundRobinIndexDetectionStrategyTest {

    @Test
    void shouldReturnAlwaysZero(){
        RoundRobinIndexDetectionStrategy strategy = new RoundRobinIndexDetectionStrategy();
        assertEquals(0, strategy.next(1));
    }
}
