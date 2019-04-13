//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.util;

public class ExponentialBackoffStrategy {
    private long delayInterval;
    private long initialDelay;
    private long maxDelay;

    public ExponentialBackoffStrategy(long delayInterval, long initialDelay, long maxDelay) {
        this.delayInterval = delayInterval;
        this.initialDelay = initialDelay;
        this.maxDelay = maxDelay;
    }

    public long delayBeforeNextRetry(int retriesAttempted) {
        if (retriesAttempted < 1) {
            return this.initialDelay;
        } else if (retriesAttempted > 63) {
            return this.maxDelay;
        } else {
            long multiplier = 1L << retriesAttempted - 1;
            if (multiplier > 9223372036854775807L / this.delayInterval) {
                return this.maxDelay;
            } else {
                long delay = multiplier * this.delayInterval;
                delay = Math.min(delay, this.maxDelay);
                return delay;
            }
        }
    }
}
