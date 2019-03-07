package com.github.mikesafonov.starter;

/**
 * Should be used for next index creation when in {@link StrategySenderManager}
 *
 * @author Mike Safonov
 */
public interface IndexDetectionStrategy {
    /**
     * Create next index based on size of connections
     *
     * @param size size of connections
     * @return next index
     */
    int next(int size);
}
