package com.github.mikesafonov.starter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of {@link IndexDetectionStrategy} which return random index based on incoming size
 *
 * @author Mike Safonov
 */
public class RandomIndexDetectionStrategy implements IndexDetectionStrategy {
    @Override
    public int next(int size) {
        return ThreadLocalRandom.current().nextInt(0, size);
    }
}
