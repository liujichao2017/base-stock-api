package com.hm.stock.domain.ws.entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class Heartbeat implements Delayed {

    private String sessionId;

    private long time;

    public Heartbeat(String sessionId) {
        this(sessionId, 20);
    }

    public Heartbeat(String sessionId, long s) {
        this.sessionId = sessionId;
        this.time = System.currentTimeMillis() + (s * 1000);
    }

    public void refresh() {
        refresh(20);
    }

    public void refresh(long s) {
        this.time = System.currentTimeMillis() + (s * 1000);
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        Heartbeat heartbeat = (Heartbeat) o;
        long diff = this.time - heartbeat.time;
        return diff > 0 ? 1 : diff == 0 ? 0 : -1;
    }
}
