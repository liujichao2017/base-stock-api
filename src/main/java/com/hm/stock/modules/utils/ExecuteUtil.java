package com.hm.stock.modules.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public enum ExecuteUtil {
    COMMON(2, 5),
    COIN_WX(5, 10),
    COIN_GEN(5, 5),
    COIN_TRADE(10, 15),
    COIN(2, 5),
    STOCK(15, 20),
    STOCK_HK(5,10),
    STOCK_CNA(5,10),
    ;
    private final Executor executor;

    ExecuteUtil(int core, int max) {
        executor = new ThreadPoolExecutor(core, max, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
