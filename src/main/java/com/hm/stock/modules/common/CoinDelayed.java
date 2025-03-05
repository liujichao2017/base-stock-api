package com.hm.stock.modules.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
public class CoinDelayed implements Delayed {

    private String symbol;
    private long time;
    private Long delegationId;

    public CoinDelayed(String symbol, long time) {
        this.time = time;
        this.symbol = symbol;
    }

    public CoinDelayed(Long delegationId, long time) {
        this.delegationId = delegationId;
        this.time = time;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        CoinDelayed item = (CoinDelayed) o;
        long diff = this.time - item.time;
        if (diff <= 0) {// 改成>=会造成问题
            return -1;
        } else {
            return 1;
        }
    }

    public static void main(String[] args) {
        DelayQueue<CoinDelayed> delayeds = new DelayQueue<>();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            delayeds.add(new CoinDelayed(i + "", l + ((11 - i) * 1000L)));
        }
        try {
            CoinDelayed peek = null;
            while ((peek = delayeds.take()) != null) {
                System.out.println(peek.getSymbol() + "-" + (peek.getTime() - l));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
