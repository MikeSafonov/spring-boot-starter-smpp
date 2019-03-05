package com.github.mikesafonov.starter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round robin strategy
 *
 * @author Mike Safonov
 */
public class RoundRobinIndexDetectionStrategy implements IndexDetectionStrategy {
    private AtomicInteger nextIndexCounter = new AtomicInteger(0);

    /**
     * Increment current index and return new index by module of {@code smscConnections} size
     *
     * @return new index
     */
    @Override
    public int next(int size) {
        for (; ; ) {
            int current = nextIndexCounter.get();
            int next = (current + 1) % size;
            if (nextIndexCounter.compareAndSet(current, next)) {
                return next;
            }
        }
    }
}
