package com.skillforge.security.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed-window rate limiter, keyed by an arbitrary caller-supplied string
 * (typically "clientIp:path"). In-memory and per-instance: adequate for a
 * single-node deployment. A horizontally-scaled deployment needs a shared
 * store instead (Redis + Bucket4j, an API gateway, etc.) since each instance
 * here tracks its own counts independently.
 */
@Component
public class InMemoryRateLimiter {

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public boolean tryConsume(String key, int maxRequests, Duration window) {
        long now = System.currentTimeMillis();
        Window w = windows.computeIfAbsent(key, k -> new Window(now));

        synchronized (w) {
            if (now - w.windowStartMillis >= window.toMillis()) {
                w.windowStartMillis = now;
                w.count.set(0);
            }
            return w.count.incrementAndGet() <= maxRequests;
        }
    }

    private static final class Window {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStartMillis;

        Window(long start) {
            this.windowStartMillis = start;
        }
    }
}
