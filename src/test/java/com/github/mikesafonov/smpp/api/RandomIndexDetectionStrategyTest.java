package com.github.mikesafonov.smpp.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomIndexDetectionStrategyTest {
    @Test
    void shouldAlwaysReturnZero() {
        RandomIndexDetectionStrategy strategy = new RandomIndexDetectionStrategy();
        assertEquals(0, strategy.next(1));
    }
}
